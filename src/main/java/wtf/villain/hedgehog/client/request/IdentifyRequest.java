package wtf.villain.hedgehog.client.request;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.person.Person;

@SuppressWarnings("unused")
public interface IdentifyRequest {

    @ApiStatus.Internal
    static void identify(@NotNull PosthogClient posthog, @NotNull Person person) {
        Event.builder()
              .name("$identify")
              .build()
              .enqueue(person, posthog);
    }

}
