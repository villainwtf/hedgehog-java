package wtf.villain.hedgehog.client.request;

import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.person.Person;

@SuppressWarnings("unused")
public interface ViewRequest {

    @ApiStatus.Internal
    static void pageView(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull String title) {
        Event.builder()
              .name("$pageview")
              .property("title", new JsonPrimitive(title))
              .build()
              .enqueue(person, posthog);
    }

    @ApiStatus.Internal
    static void screenView(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull String screenName) {
        Event.builder()
              .name("$screen")
              .property("$screen_name", new JsonPrimitive(screenName))
              .build()
              .enqueue(person, posthog);
    }

}
