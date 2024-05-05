package wtf.villain.hedgehog.client.internal;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum PosthogRequest {

    CAPTURE_EVENT("POST", "capture"),
    CAPTURE_BATCH("POST", "batch"),
    EVALUATE_FEATURE_FLAGS("POST", "decide?v=3"),
    GET_EARLY_ACCESS_FEATURES("GET", "api/early_access_features", true),
    OTHER(null, null);

    private final String method, endpoint;
    private final boolean hasExtension;

    PosthogRequest(@Nullable String method, @Nullable String endpoint) {
        this.method = method;
        this.endpoint = endpoint;
        this.hasExtension = false;
    }

    PosthogRequest(@Nullable String method, @Nullable String endpoint, boolean hasExtension) {
        this.method = method;
        this.endpoint = endpoint;
        this.hasExtension = hasExtension;
    }
}
