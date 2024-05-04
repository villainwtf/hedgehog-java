package wtf.villain.hedgehog.client.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.data.featureflag.FeatureFlag;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagCollection;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagData;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.util.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface FeatureFlagRequest {

    @ApiStatus.Internal
    @NotNull
    static CompletableFuture<FeatureFlagCollection> evaluateFeatureFlags(@NotNull PosthogClient posthog, @NotNull Person person) {
        var json = Json.builder()
              .add("api_key", posthog.apiKey())
              .add("distinct_id", person.distinctId())
              .add("person_properties", Json.of(person.buildProperties(true, true, false)))
              .build();

        var future = new CompletableFuture<JsonElement>();

        posthog.queueWorker().enqueue(new QueuedRequest(
              PosthogRequest.EVALUATE_FEATURE_FLAGS,
              json,
              true,
              Optional.of(future)
        ));

        return future.thenApplyAsync(element -> {
            var response = new Gson().fromJson(element, PartialFeatureFlagResponse.class);

            if (response.errorComputingFlags) {
                throw new RuntimeException("Error computing feature flags");
            }

            var flags = new HashMap<String, FeatureFlag>();

            response.featureFlags.forEach((key, value) -> {
                var payload = response.featureFlagPayloads.get(key);

                flags.put(key, new FeatureFlag(
                      new FeatureFlagData(value),
                      payload == null ? Optional.empty() : Optional.of(new FeatureFlagData(payload))
                ));
            });

            return new FeatureFlagCollection(flags);
        });
    }

    class PartialFeatureFlagResponse {
        protected boolean errorComputingFlags;
        protected Map<String, JsonElement> featureFlags;
        protected Map<String, JsonElement> featureFlagPayloads;
    }

}
