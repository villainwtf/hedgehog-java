package wtf.villain.hedgehog.client.modifier;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.internal.QueuedRequest;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface ResponseHandler {

    /**
     * Creates a simple response handler that completes the given future.
     *
     * @param future the future to complete
     * @return the response handler
     */
    @NotNull
    static ResponseHandler jsonFuture(@NotNull CompletableFuture<JsonElement> future) {
        return new ResponseHandler() {
            @Override
            public void onError(@NotNull QueuedRequest request, @NotNull Throwable error) {
                future.completeExceptionally(error);
            }

            @Override
            public void onSuccess(@NotNull QueuedRequest request, int code, @NotNull ResponseBody body) {
                try {
                    var json = new Gson().fromJson(body.charStream(), JsonElement.class);
                    future.complete(json);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        };
    }

    /**
     * Creates a simple response handler that completes the given future.
     *
     * @param future the future to complete
     * @return the response handler
     */
    @NotNull
    static ResponseHandler simpleFuture(@NotNull CompletableFuture<Void> future) {
        return new ResponseHandler() {
            @Override
            public void onError(@NotNull QueuedRequest request, @NotNull Throwable error) {
                future.completeExceptionally(error);
            }

            @Override
            public void onSuccess(@NotNull QueuedRequest request, int code, @NotNull ResponseBody body) {
                future.complete(null);
            }
        };
    }

    /**
     * Called when a request fails.
     *
     * @param request the request that failed
     * @param error   the error that occurred
     */
    void onError(@NotNull QueuedRequest request, @NotNull Throwable error);

    /**
     * Called when a request succeeds.
     *
     * @param request the request that succeeded
     * @param code    the response code
     * @param body    the response body
     */
    void onSuccess(@NotNull QueuedRequest request, int code, @NotNull ResponseBody body);

}
