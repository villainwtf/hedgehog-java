package wtf.villain.hedgehog.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.request.EventRequest;
import wtf.villain.hedgehog.client.request.FeatureFlagRequest;
import wtf.villain.hedgehog.client.request.IdentifyRequest;
import wtf.villain.hedgehog.client.request.ViewRequest;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagCollection;
import wtf.villain.hedgehog.data.person.Person;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Getter
public class PosthogClient {

    @NotNull
    public static PosthogClientBuilder builder() {
        return new PosthogClientBuilder();
    }

    private final String apiKey;
    private final QueueWorker queueWorker;

    protected PosthogClient(@NotNull String baseUrl, @NotNull String apiKey) {
        this.apiKey = apiKey;
        this.queueWorker = new QueueWorker(baseUrl);
    }

    public void shutdown() {
        queueWorker.shutdown();
    }

    public void enqueueEvent(@NotNull Event event, @NotNull Person person) {
        EventRequest.enqueueEvent(this, event, person);
    }

    @NotNull
    public CompletableFuture<Void> captureEvent(@NotNull Event event, @NotNull Person person) {
        return EventRequest.captureEvent(this, event, person);
    }

    @NotNull
    public CompletableFuture<FeatureFlagCollection> featureFlags(@NotNull Person person) {
        return FeatureFlagRequest.evaluateFeatureFlags(this, person)
              .thenApply(collection -> {
                  person.storedFeatureFlags(Optional.of(collection));
                  return collection;
              });
    }

    public void identify(@NotNull Person person) {
        IdentifyRequest.identify(this, person);
    }

    public void enqueuePageViewEvent(@NotNull Person person, @NotNull String title) {
        ViewRequest.pageView(this, person, title);
    }

    public void enqueueScreenViewEvent(@NotNull Person person, @NotNull String screenName) {
        ViewRequest.screenView(this, person, screenName);
    }
}
