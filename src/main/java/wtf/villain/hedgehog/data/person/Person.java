package wtf.villain.hedgehog.data.person;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagCollection;
import wtf.villain.hedgehog.util.Json;

import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("unused")
public class Person {

    private static final String ANONYMOUS_DISTINCT_ID = "$hedgehog_anonymous";

    private static final Person UNIDENTIFIED_PERSON = new Person(
        ANONYMOUS_DISTINCT_ID,
        null,
        null
    );

    @NotNull
    public static Person unidentified() {
        return UNIDENTIFIED_PERSON;
    }

    @NotNull
    public static Person person(@NotNull String distinctId) {
        return new Person(distinctId, null, null);
    }

    @NotNull
    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    private final String distinctId;
    private final Map<String, JsonElement> properties;

    @Setter
    private FeatureFlagCollection storedFeatureFlags;

    private String clientIp;
    private boolean alwaysIncludePropertiesInEvents = false;

    protected Person(@NotNull String distinctId,
                     @Nullable Map<String, JsonElement> properties,
                     @Nullable String clientIp) {
        this.distinctId = distinctId;
        this.properties = properties;
        this.clientIp = clientIp;
    }

    public void setClientIp(@NotNull String clientIp) {
        this.clientIp = clientIp;
    }

    public void setAlwaysIncludePropertiesInEvents(boolean alwaysIncludePropertiesInEvents) {
        this.alwaysIncludePropertiesInEvents = alwaysIncludePropertiesInEvents;
    }

    public boolean isAnonymous() {
        return ANONYMOUS_DISTINCT_ID.equals(this.distinctId);
    }

    @ApiStatus.Internal
    @NotNull
    public Map<String, JsonElement> buildProperties(@NotNull PropertyFilter filter) {
        var properties = new HashMap<String, JsonElement>();

        if (filter.includePersonProperties() && this.properties != null) {
            if (filter.useSetSyntax()) {
                properties.put("$set", Json.of(this.properties));
            } else {
                properties.putAll(this.properties);
            }
        }

        if (filter.includeIp() && this.clientIp != null) {
            properties.put("$ip", new JsonPrimitive(this.clientIp));
        }

        if (filter.includeFeatureFlags() && this.storedFeatureFlags != null) {
            this.storedFeatureFlags.forEach((key, value) -> {
                properties.put("$feature/" + key, new JsonPrimitive(value.variantAsString()));
            });
        }

        return properties;
    }
}
