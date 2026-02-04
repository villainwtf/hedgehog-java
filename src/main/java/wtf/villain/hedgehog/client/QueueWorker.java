package wtf.villain.hedgehog.client;

import lombok.SneakyThrows;
import okhttp3.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.client.error.PosthogRequestException;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.util.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class QueueWorker {

    private static final int MAX_BATCH_SIZE = 100;

    private final PosthogClient posthogClient;
    private final OkHttpClient client;

    private final Queue<QueuedRequest> queue = new ConcurrentLinkedQueue<>();
    private final Thread queueThread;

    protected QueueWorker(@NotNull PosthogClient posthogClient) {
        this.posthogClient = posthogClient;
        this.client = new OkHttpClient();

        queueThread = new Thread(this::work);
        queueThread.setDaemon(true);
        queueThread.setName("hedgehog-queue-worker");
        queueThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            queueThread.interrupt();

            flushQueue(true);
        }));
    }

    protected void shutdown(boolean await) {
        queueThread.interrupt();

        flushQueue(true);

        client.connectionPool().evictAll();
        client.dispatcher().executorService().shutdown();
    }

    @ApiStatus.Internal
    public void enqueue(@NotNull QueuedRequest request) {
        if (request.immediate() || request.request() != PosthogRequest.CAPTURE_EVENT) {
            dispatchRequest(request, null);
            return;
        }

        queue.add(request);
    }

    @SuppressWarnings("BusyWait")
    private void work() {
        while (!Thread.interrupted()) {
            flushQueue(false);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    void flushQueue(boolean await) {
        var batchCapture = new ArrayList<QueuedRequest>();
        var pending = new ArrayList<CompletableFuture<Void>>();

        QueuedRequest queued;
        while ((queued = queue.poll()) != null) {
            do {
                batchCapture.add(queued);
            } while (batchCapture.size() < MAX_BATCH_SIZE && (queued = queue.poll()) != null);

            if (await) {
                var future = new CompletableFuture<Void>();
                pending.add(future);
                sendBatch(batchCapture, future);
            } else {
                sendBatch(batchCapture, null);
            }

            batchCapture.clear();
        }

        if (await && !pending.isEmpty()) {
            CompletableFuture.allOf(pending.toArray(new CompletableFuture[0]))
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> null)
                .join();
        }
    }

    private void sendBatch(@NotNull List<QueuedRequest> batchCapture, CompletableFuture<Void> future) {
        // The client adds the API key to each event, so we can just take it from the first event.
        var apiKey = batchCapture.get(0).body().getAsJsonObject().get("api_key").getAsString();

        var json = Json.builder()
            .add("api_key", apiKey)
            .add("batch", Json
                .array()
                .use(array -> batchCapture.forEach(request -> array.add(request.body()))))
            .build();

        var request = new QueuedRequest(
            PosthogRequest.CAPTURE_BATCH,
            json,
            false);

        dispatchRequest(request, future);
    }

    private void dispatchRequest(@NotNull QueuedRequest request, @Nullable CompletableFuture<Void> dispatchFuture) {
        var method = request.method();
        var url = posthogClient.baseUrl() + "/" + request.endpoint();

        var body = request.body();
        var httpBody = body.isJsonNull()
            ? null
            : RequestBody.create(body.toString(), MediaType.parse("application/json"));

        var httpRequestBuilder = new Request.Builder()
            .method(method, httpBody)
            .url(url)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");

        var modifier = posthogClient.requestModifier();
        if (modifier != null) {
            httpRequestBuilder = modifier.modify(httpRequestBuilder);

            if (httpRequestBuilder == null) {
                return;
            }
        }

        var httpRequest = httpRequestBuilder.build();

        var handler = request.handler() == null
            ? posthogClient.defaultResponseHandler()
            : request.handler();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (dispatchFuture != null) {
                    dispatchFuture.complete(null);
                }

                if (handler != null) {
                    handler.onError(request, e);
                }
            }

            @SneakyThrows
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    var code = response.code();
                    var body = response.body();

                    if (code >= 200 && code < 300) {
                        assert body != null;

                        if (handler != null) {
                            handler.onSuccess(request, code, body);
                        }
                    } else if (handler != null) {
                        handler.onError(request, new PosthogRequestException(code, body));
                    }
                } finally {
                    if (dispatchFuture != null) {
                        dispatchFuture.complete(null);
                    }
                }
            }
        });
    }
}
