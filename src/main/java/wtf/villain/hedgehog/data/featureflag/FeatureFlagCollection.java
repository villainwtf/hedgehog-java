package wtf.villain.hedgehog.data.featureflag;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings({"unused"})
public record FeatureFlagCollection(@NotNull Map<String, FeatureFlag> flags) {

    public void forEach(@NotNull BiConsumer<String, FeatureFlag> action) {
        flags.forEach(action);
    }

    @NotNull
    public Optional<FeatureFlag> get(@NotNull String key) {
        return Optional.ofNullable(flags.get(key));
    }

    @NotNull
    public Optional<String> getStringFlag(@NotNull String key) {
        return get(key).flatMap(FeatureFlag::payloadAsString);
    }

    @NotNull
    public Optional<Boolean> getBooleanFlag(@NotNull String key) {
        return get(key).flatMap(FeatureFlag::payloadAsBoolean);
    }

    @NotNull
    public Optional<Integer> getIntegerFlag(@NotNull String key) {
        return get(key).flatMap(FeatureFlag::payloadAsInteger);
    }

    @NotNull
    public Optional<JsonElement> getJsonFlag(@NotNull String key) {
        return get(key).flatMap(FeatureFlag::payloadAsJson);
    }

    @NotNull
    public <T> Optional<T> getTypedJsonFlag(@NotNull String key, @NotNull Class<T> type) {
        return get(key).flatMap(FeatureFlag::payloadAsJson).map(json -> new Gson().fromJson(json, type));
    }

}
