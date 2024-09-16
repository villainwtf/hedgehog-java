package wtf.villain.hedgehog.data.event;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
public class EventBuilder {

    private Optional<String> name = Optional.empty();
    private final Map<String, JsonElement> properties = new HashMap<>();
    private boolean isIdentify = false;

    @Contract("_ -> this")
    @NotNull
    public EventBuilder name(@NotNull String name) {
        this.name = Optional.of(name);

        if ("$identify".equals(name)) {
            isIdentify = true;
        }

        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public EventBuilder property(@NotNull String key, @NotNull JsonElement value) {
        properties.put(key, value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public EventBuilder properties(@NotNull Map<String, JsonElement> properties) {
        this.properties.putAll(properties);
        return this;
    }

    @Contract(" -> this")
    @NotNull
    public EventBuilder identify() {
        isIdentify = true;
        return this;
    }

    @NotNull
    public Event build() {
        var name = this.name.orElseThrow(() -> new IllegalStateException("Event name must be set"));

        return new Event(
            name,
            properties.isEmpty() ? Optional.empty() : Optional.of(properties),
            isIdentify
        );
    }

}
