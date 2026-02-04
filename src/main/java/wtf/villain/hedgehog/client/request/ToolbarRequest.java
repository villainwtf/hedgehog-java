package wtf.villain.hedgehog.client.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.client.modifier.ResponseHandler;
import wtf.villain.hedgehog.data.toolbar.ToolbarFeatureFlag;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface ToolbarRequest {

    @ApiStatus.Internal
    static CompletableFuture<List<ToolbarFeatureFlag>> toolbarGetFeatureFlags(@NotNull PosthogClient posthog, @NotNull String groups, @NotNull String temporaryToken) {
        var future = new CompletableFuture<JsonElement>();

        posthog.queueWorker().enqueue(new QueuedRequest(
            PosthogRequest.OTHER,
            "GET",
            "/api/projects/@current/feature_flags/my_flags?groups=" + groups + "&temporary_token=" + temporaryToken,
            null,
            JsonNull.INSTANCE,
            true,
            ResponseHandler.jsonFuture(future)
        ));

        return future.thenApplyAsync(element -> {
            var response = new Gson().fromJson(element, ToolbarFeatureFlag[].class);
            return List.of(response);
        });
    }
}
