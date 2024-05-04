package wtf.villain.hedgehog.data.featureflag;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public record FeatureFlagData(@NotNull FeatureFlagDataType type, @NotNull JsonElement value) {

    public FeatureFlagData(@NotNull JsonElement element) {
        this(FeatureFlagDataType.determineType(element), element);
    }

    @NotNull
    public String asString() {
        return switch (type) {
            case STRING -> value.getAsString();
            case BOOLEAN -> String.valueOf(value.getAsBoolean());
            case INTEGER -> String.valueOf(value.getAsInt());
            case JSON -> value.toString();
        };
    }

    public boolean asBoolean() {
        return switch (type) {
            case STRING -> Boolean.parseBoolean(value.getAsString());
            case BOOLEAN -> value.getAsBoolean();
            case INTEGER -> value.getAsInt() != 0;
            case JSON -> false;
        };
    }
}
