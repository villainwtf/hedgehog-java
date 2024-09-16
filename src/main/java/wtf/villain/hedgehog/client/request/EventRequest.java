package wtf.villain.hedgehog.client.request;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.client.internal.PosthogRequest;
import wtf.villain.hedgehog.client.internal.QueuedRequest;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.util.Json;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface EventRequest {

    @ApiStatus.Internal
    static void enqueueEvent(@NotNull PosthogClient posthog, @NotNull Event event, @NotNull Person person) {
        var eventJson = getEventJson(posthog, event, person);

        posthog.queueWorker().enqueue(new QueuedRequest(
            PosthogRequest.CAPTURE_EVENT,
            eventJson,
            false));
    }

    @ApiStatus.Internal
    @NotNull
    static CompletableFuture<Void> captureEvent(@NotNull PosthogClient posthog, @NotNull Event event, @NotNull Person person) {
        var eventJson = getEventJson(posthog, event, person);

        var future = new CompletableFuture<JsonElement>();

        posthog.queueWorker().enqueue(new QueuedRequest(
            PosthogRequest.CAPTURE_EVENT,
            eventJson,
            true,
            Optional.of(future)));

        return future.thenApplyAsync(json -> null);
    }

    @ApiStatus.Internal
    @NotNull
    private static JsonElement getEventJson(@NotNull PosthogClient posthog, @NotNull Event event, @NotNull Person person) {
        return Json.builder()
            .add("api_key", posthog.apiKey())
            .add("uuid", UUID.randomUUID().toString())
            .add("timestamp", Instant.now().toString())
            .add("distinct_id", person.distinctId())
            .add("event", event.name())
            .add("properties", Json.of(event.buildProperties(person)))
            .build();
    }

}
