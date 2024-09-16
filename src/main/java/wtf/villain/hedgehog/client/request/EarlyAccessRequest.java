package wtf.villain.hedgehog.client.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.data.earlyaccess.EarlyAccessFeature;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.util.Json;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface EarlyAccessRequest {

    @ApiStatus.Internal
    static void enqueueEarlyAccessFeatureEnrollment(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull String feature, boolean isEnrolled) {
        Event.builder()
            .name("$feature_enrollment_update")
            .property("$feature_flag", new JsonPrimitive(feature))
            .property("$feature_enrollment", new JsonPrimitive(isEnrolled))
            .property("$set", Json.builder()
                .add("$feature_enrollment/" + feature, new JsonPrimitive(isEnrolled))
                .build())
            .build()
            .enqueue(person, posthog);
    }

    @ApiStatus.Internal
    @NotNull
    static CompletableFuture<Void> updateEarlyAccessFeatureEnrollmentImmediately(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull String feature, boolean isEnrolled) {
        return Event.builder()
            .name("$feature_enrollment_update")
            .property("$feature_flag", new JsonPrimitive(feature))
            .property("$feature_enrollment", new JsonPrimitive(isEnrolled))
            .property("$set", Json.builder()
                .add("$feature_enrollment/" + feature, new JsonPrimitive(isEnrolled))
                .build())
            .build()
            .capture(person, posthog);
    }

    @ApiStatus.Internal
    @NotNull
    static CompletableFuture<List<EarlyAccessFeature>> earlyAccessFeatures(@NotNull PosthogClient posthog) {
        var future = new CompletableFuture<JsonElement>();

        posthog.queueWorker().enqueue(new QueuedRequest(
            PosthogRequest.GET_EARLY_ACCESS_FEATURES,
            null,
            null,
            "?api_key=" + posthog.apiKey(),
            JsonNull.INSTANCE,
            true,
            Optional.of(future)
        ));

        return future.thenApplyAsync(element -> {
            var json = new Gson().fromJson(element, PartialEarlyAccessFeaturesResponse.class);
            return json.earlyAccessFeatures;
        });
    }

    class PartialEarlyAccessFeaturesResponse {
        protected List<EarlyAccessFeature> earlyAccessFeatures;
    }

}
