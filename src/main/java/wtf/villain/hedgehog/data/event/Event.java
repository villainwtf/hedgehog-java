package wtf.villain.hedgehog.data.event;

import com.google.gson.JsonElement;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.data.person.PropertyFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
public class Event {

    @NotNull
    public static EventBuilder builder() {
        return new EventBuilder();
    }

    private final String name;
    private final Optional<Map<String, JsonElement>> properties;
    private final boolean isIdentify;

    protected Event(@NotNull String name,
                    @NotNull Optional<Map<String, JsonElement>> properties,
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
        this.properties.ifPresent(properties::putAll);

        var personEventProperties = person.buildProperties(PropertyFilter.create()
              .includePersonProperties(isIdentify)
              .useSetSyntax(isIdentify)
              .includeIp(true)
              .includeFeatureFlags(true));
        properties.putAll(personEventProperties);

        return properties;
    }

}
