package wtf.villain.hedgehog.data.person;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
public class Person {

    @NotNull
    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    private final String distinctId;
    private final Optional<Map<String, JsonElement>> properties;

    @Setter
    private Optional<FeatureFlagCollection> storedFeatureFlags;

    private Optional<String> clientIp;

    protected Person(@NotNull String distinctId,
                     @NotNull Optional<Map<String, JsonElement>> properties,
                     @NotNull Optional<String> clientIp) {
        this.distinctId = distinctId;
        this.properties = properties;
        this.storedFeatureFlags = Optional.empty();
        this.clientIp = clientIp;
    }

    public void setClientIp(@NotNull String clientIp) {
        this.clientIp = Optional.of(clientIp);
    }

    @ApiStatus.Internal
    @NotNull
    public Map<String, JsonElement> buildProperties(
          boolean includePersonProperties, boolean includeIp, boolean includeFeatureFlags) {
        var properties = new HashMap<String, JsonElement>();

        if (includePersonProperties) {
            this.properties.ifPresent(properties::putAll);
        }

        if (includeIp) {
            clientIp.ifPresent(ip -> properties.put("$ip", new JsonPrimitive(ip)));
        }

        if (includeFeatureFlags) {
            storedFeatureFlags.ifPresent(flags -> {
                flags.forEach((key, value) -> properties.put("$feature/" + key, new JsonPrimitive(value.variantAsString())));
            });
        }

        return properties;
    }
}
