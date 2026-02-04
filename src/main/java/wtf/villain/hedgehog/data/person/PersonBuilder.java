package wtf.villain.hedgehog.data.person;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PersonBuilder {

    private String distinctId;
    private final Map<String, JsonElement> properties = new HashMap<>();
    private String clientIp;
    private boolean alwaysIncludePropertiesInEvents = false;

    @Contract("_ -> this")
    @NotNull
    public PersonBuilder distinctId(@NotNull String distinctId) {
        this.distinctId = distinctId;
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public PersonBuilder property(@NotNull String key, @NotNull JsonElement value) {
        properties.put(key, value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PersonBuilder properties(@NotNull Map<String, JsonElement> properties) {
        this.properties.putAll(properties);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PersonBuilder clientIp(@NotNull String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PersonBuilder alwaysIncludePropertiesInEvents(boolean alwaysIncludePropertiesInEvents) {
        this.alwaysIncludePropertiesInEvents = alwaysIncludePropertiesInEvents;
        return this;
    }

    @NotNull
    public Person build() {
        if (this.distinctId == null || this.distinctId.isBlank()) {
            throw new IllegalStateException("Distinct ID is required");
        }

        var person = new Person(
            distinctId,
            properties,
            clientIp
        );

        person.setAlwaysIncludePropertiesInEvents(alwaysIncludePropertiesInEvents);

        return person;
    }
}
