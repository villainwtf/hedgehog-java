package wtf.villain.hedgehog.client.error;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@SuppressWarnings("unused")
public class PosthogRequestException extends RuntimeException {

    @NotNull
    private static String stringifyBody(@NotNull ResponseBody body) {
        try {
            return body.string();
        } catch (IOException e) {
            return "Failed to read response body: " + e.getMessage();
        }
    }

    private final int statusCode;
    private final String responseBody;

    @ApiStatus.Internal
    public PosthogRequestException(int statusCode, @Nullable ResponseBody body) {
        this(statusCode, body == null ? "No body" : stringifyBody(body));
    }

    public PosthogRequestException(int statusCode, @NotNull String responseBody) {
        super("Posthog request failed with status code " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int statusCode() {
        return statusCode;
    }

    @NotNull
    public String responseBody() {
        return responseBody;
    }
}
