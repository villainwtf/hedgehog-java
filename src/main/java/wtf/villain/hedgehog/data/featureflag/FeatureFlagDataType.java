package wtf.villain.hedgehog.data.featureflag;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum FeatureFlagDataType {
    BOOLEAN,
    INTEGER,
    STRING,
    JSON,
    ;

    @ApiStatus.Internal
    @NotNull
    public static FeatureFlagDataType determineType(@NotNull JsonElement element) {
        if (element.isJsonPrimitive()) {
            var primitive = element.getAsJsonPrimitive();

            if (primitive.isBoolean()) {
                return BOOLEAN;
            } else if (primitive.isNumber()) {
                return INTEGER;
            } else if (primitive.isString()) {
                return STRING;
            }
        }

        return JSON;
    }
}
