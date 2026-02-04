package wtf.villain.hedgehog.client.internal;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.client.modifier.ResponseHandler;

import java.util.Objects;

@SuppressWarnings("unused")
public record QueuedRequest(@NotNull PosthogRequest request,
                            @Nullable String otherMethod,
                            @Nullable String otherEndpoint,
                            @Nullable String endpointExtension,
                            @NotNull JsonElement body,
                            boolean immediate,
                            @Nullable ResponseHandler handler) {

    public QueuedRequest(@NotNull PosthogRequest request,
                         @NotNull JsonElement body,
                         boolean immediate,
                         @Nullable ResponseHandler handler) {
        this(request, null, null, null, body, immediate, handler);

        if (request.method() == null || request.endpoint() == null) {
            throw new IllegalArgumentException("PosthogRequest must have a method and endpoint defined.");
        }
    }

    public QueuedRequest(@NotNull PosthogRequest request,
                         @NotNull JsonElement body,
                         boolean immediate) {
        this(request, body, immediate, null);
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
            return request.endpoint() + (request.hasExtension() ? endpointExtension : "");
        }

        return Objects.requireNonNull(otherEndpoint);
    }
}
