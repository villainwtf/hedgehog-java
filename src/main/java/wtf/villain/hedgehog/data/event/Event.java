package wtf.villain.hedgehog.data.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.data.person.PropertyFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings("unused")
public class Event {

    @NotNull
    public static EventBuilder builder() {
        return new EventBuilder();
    }

    private final String name;
    private final Map<String, JsonElement> properties;
    private final boolean isIdentify;

    protected Event(@NotNull String name,
                    @Nullable Map<String, JsonElement> properties,
                    boolean isIdentify) {
        this.name = name;
        this.properties = properties;
        this.isIdentify = isIdentify;
    }

    @NotNull
    public CompletableFuture<Void> capture(@NotNull Person person, @NotNull PosthogClient posthog) {
        return posthog.captureEvent(this, person);
    }

    public void enqueue(@NotNull Person person, @NotNull PosthogClient posthog) {
        posthog.enqueueEvent(this, person);
    }

    @ApiStatus.Internal
    @NotNull
    public Map<String, JsonElement> buildProperties(@NotNull Person person) {
        var properties = new HashMap<String, JsonElement>();

        if (this.properties != null) {
            properties.putAll(this.properties);
        }

        if (person.isAnonymous()) {
            properties.put("$process_person_profile", new JsonPrimitive(false));
        } else {
            var personEventProperties = person.buildProperties(PropertyFilter.create()
                .includePersonProperties(isIdentify || person.alwaysIncludePropertiesInEvents())
                .useSetSyntax(isIdentify)
                .includeIp(true)
                .includeFeatureFlags(true));

            properties.putAll(personEventProperties);
        }

        return properties;
    }

}
