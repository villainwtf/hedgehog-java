package wtf.villain.hedgehog.client.internal;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public record QueuedRequest(@NotNull PosthogRequest request,
                            @Nullable String otherMethod,
                            @Nullable String otherEndpoint,
                            @NotNull JsonElement body,
                            boolean immediate,
                            @NotNull Optional<CompletableFuture<JsonElement>> responseFuture) {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public QueuedRequest(@NotNull PosthogRequest request,
                         @NotNull JsonElement body,
                         boolean immediate,
                         @NotNull Optional<CompletableFuture<JsonElement>> responseFuture) {
        this(request, null, null, body, immediate, responseFuture);

        if (request.method() == null || request.endpoint() == null) {
            throw new IllegalArgumentException("PosthogRequest must have a method and endpoint defined.");
        }
    }

    public QueuedRequest(@NotNull PosthogRequest request,
                         @NotNull JsonElement body,
                         boolean immediate) {
        this(request, body, immediate, Optional.empty());
    }

    @NotNull
    public String method() {
        if (request.method() != null) {
            return request.method();
        }

        return Objects.requireNonNull(otherMethod);
    }

    @NotNull
    public String endpoint() {
        if (request.endpoint() != null) {
            return request.endpoint();
        }

        return Objects.requireNonNull(otherEndpoint);
    }
}