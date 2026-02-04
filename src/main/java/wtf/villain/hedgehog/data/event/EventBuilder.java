package wtf.villain.hedgehog.data.event;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class EventBuilder {

    private String name;
    private final Map<String, JsonElement> properties = new HashMap<>();
    private boolean isIdentify = false;

    @Contract("_ -> this")
    @NotNull
    public EventBuilder name(@NotNull String name) {
        this.name = name;

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
        if (this.name == null || this.name.isBlank()) {
            throw new IllegalStateException("Event name must be set");
        }

        return new Event(
            name,
            properties,
            isIdentify
        );
    }

}
