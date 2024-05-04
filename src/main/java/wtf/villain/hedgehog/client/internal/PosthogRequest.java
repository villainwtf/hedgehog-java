package wtf.villain.hedgehog.client.internal;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum PosthogRequest {

    CAPTURE_EVENT("POST", "/capture"),
    CAPTURE_BATCH("POST", "/batch"),
    EVALUATE_FEATURE_FLAGS("POST", "/decide?v=3"),
    OTHER(null, null);

    private final String method, endpoint;

    PosthogRequest(@Nullable String method, @Nullable String endpoint) {
        this.method = method;
        this.endpoint = endpoint;
    }

}
