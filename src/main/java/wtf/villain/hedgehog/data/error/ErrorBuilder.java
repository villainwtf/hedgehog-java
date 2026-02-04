package wtf.villain.hedgehog.data.error;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ErrorBuilder {

    private Throwable throwable;
    private final Map<String, JsonElement> properties = new HashMap<>();
    private boolean isHandled, isSynthetic;

    @Contract("_ -> this")
    @NotNull
    public ErrorBuilder throwable(@NotNull Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public ErrorBuilder property(@NotNull String key, @NotNull JsonElement value) {
        properties.put(key, value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ErrorBuilder properties(@NotNull Map<String, JsonElement> properties) {
        this.properties.putAll(properties);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ErrorBuilder handled(boolean isHandled) {
        this.isHandled = isHandled;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ErrorBuilder synthetic(boolean isSynthetic) {
        this.isSynthetic = isSynthetic;
        return this;
    }

    @NotNull
    public Error build() {
        if (this.throwable == null) {
            throw new IllegalStateException("Error throwable must be set");
        }

        return new Error(
            throwable,
            properties,
            isHandled,
            isSynthetic
        );
    }

}
