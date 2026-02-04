package wtf.villain.hedgehog.data.error;

import com.google.gson.JsonElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.data.person.Person;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings("unused")
public class Error {

    @NotNull
    public static Error error(@NotNull Throwable throwable) {
        return new Error(throwable, null, false, false);
    }

    @NotNull
    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }

    private final Throwable throwable;
    private final Map<String, JsonElement> properties;
    private final boolean isHandled, isSynthetic;

    protected Error(@NotNull Throwable throwable,
                    @Nullable Map<String, JsonElement> properties,
                    boolean isHandled,
                    boolean isSynthetic) {
        this.throwable = throwable;
        this.properties = properties;
        this.isHandled = isHandled;
        this.isSynthetic = isSynthetic;
    }

    @NotNull
    public CompletableFuture<Void> capture(@NotNull Person person, @NotNull PosthogClient posthog) {
        return posthog.captureError(this, person);
    }

    public void enqueue(@NotNull Person person, @NotNull PosthogClient posthog) {
        posthog.enqueueError(this, person);
    }

}
