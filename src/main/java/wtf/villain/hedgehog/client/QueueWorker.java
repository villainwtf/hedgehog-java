package wtf.villain.hedgehog.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import okhttp3.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.error.PosthogRequestException;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.util.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
public class QueueWorker {

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
            flushQueue();
        }));
    }

    protected void shutdown() {
        queueThread.interrupt();
        flushQueue();
        client.dispatcher().executorService().shutdown();
    }

    @ApiStatus.Internal
    public void enqueue(@NotNull QueuedRequest request) {
        if (request.immediate() || request.request() != PosthogRequest.CAPTURE_EVENT) {
            dispatchRequest(request);
            return;
        }

        queue.add(request);
    }

    @SuppressWarnings("BusyWait")
    private void work() {
        while (!Thread.interrupted()) {
            flushQueue();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void flushQueue() {
        var batchCapture = new ArrayList<QueuedRequest>();

        QueuedRequest queued;
        while ((queued = queue.poll()) != null) {
            batchCapture.add(queued);
        }

        if (!batchCapture.isEmpty()) {
            // The API key is added by the client to each event, so we can just take it from the first event.
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

            dispatchRequest(request);
        }
    }

    private void dispatchRequest(@NotNull QueuedRequest request) {
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

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                request.responseFuture().ifPresent(future ->
                    future.completeExceptionally(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                var code = response.code();
                var body = response.body();

                if (code >= 200 && code < 300) {
                    assert body != null;
                    var jsonBody = new Gson().fromJson(body.charStream(), JsonElement.class);

                    request.responseFuture().ifPresent(future ->
                        future.complete(jsonBody));
                } else {
                    request.responseFuture().ifPresent(future ->
                        future.completeExceptionally(new PosthogRequestException(code, body)));
                }
            }
        });
    }
}
