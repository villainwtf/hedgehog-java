package wtf.villain.hedgehog.data.featureflag;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings({"unused"})
public record FeatureFlag(@NotNull FeatureFlagData variant,
                          @NotNull Optional<FeatureFlagData> payload) {

    @Override
    public FeatureFlagData variant() {
        return variant;
    }

    @NotNull
    public String variantAsString() {
        return variant.asString();
    }

    public boolean variantAsBoolean() {
        return variant.asBoolean();
    }

    @Override
    public Optional<FeatureFlagData> payload() {
        return payload;
    }

    @NotNull
    public Optional<String> payloadAsString() {
        return payload
            .filter(d -> d.type() == FeatureFlagDataType.STRING)
            .map(d -> d.value().getAsString());
    }

    @NotNull
    public Optional<Boolean> payloadAsBoolean() {
        return payload
            .filter(d -> d.type() == FeatureFlagDataType.BOOLEAN)
            .map(d -> d.value().getAsBoolean());
    }

    @NotNull
    public Optional<Integer> payloadAsInteger() {
        return payload
            .filter(d -> d.type() == FeatureFlagDataType.INTEGER)
            .map(d -> d.value().getAsInt());
    }

    @NotNull
    public Optional<JsonElement> payloadAsJson() {
        return payload
            .filter(d -> d.type() == FeatureFlagDataType.JSON)
            .map(FeatureFlagData::value);
    }

}
