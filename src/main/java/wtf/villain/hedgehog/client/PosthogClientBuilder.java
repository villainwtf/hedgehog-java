package wtf.villain.hedgehog.client;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.api.PosthogServerFeature;
import wtf.villain.hedgehog.client.modifier.ErrorModifier;
import wtf.villain.hedgehog.client.modifier.RequestModifier;
import wtf.villain.hedgehog.client.modifier.ResponseHandler;

import java.util.Arrays;
import java.util.EnumSet;

@SuppressWarnings("unused")
public class PosthogClientBuilder {

    private String baseUrl;
    private String apiKey;

    private final EnumSet<PosthogServerFeature> serverFeatures = EnumSet.noneOf(PosthogServerFeature.class);

    private RequestModifier requestModifier;
    private ResponseHandler defaultResponseHandler;
    private ErrorModifier errorModifier;

    protected PosthogClientBuilder() {
    }

    /**
     * Set the base URL of the Posthog server.
     *
     * @param baseUrl the base URL of the Posthog server
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder baseUrl(@NotNull String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Set the API key for authentication with the Posthog server.
     *
     * @param apiKey the API key for authentication
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder apiKey(@NotNull String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Add server features to the client configuration.
     *
     * @param features the server features to add
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder serverFeatures(@NotNull PosthogServerFeature... features) {
        serverFeatures.addAll(Arrays.asList(features));
        return this;
    }

    /**
     * Set the request modifier for customizing outgoing requests.
     *
     * @param requestModifier the request modifier to use
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder requestModifier(@NotNull RequestModifier requestModifier) {
        this.requestModifier = requestModifier;
        return this;
    }

    /**
     * Set the default response handler for processing incoming responses.
     *
     * @param defaultResponseHandler the default response handler to use
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder defaultResponseHandler(@NotNull ResponseHandler defaultResponseHandler) {
        this.defaultResponseHandler = defaultResponseHandler;
        return this;
    }

    /**
     * Set the error modifier for customizing error handling.
     *
     * @param errorModifier the error modifier to use
     * @return this builder instance
     */
    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder errorModifier(@NotNull ErrorModifier errorModifier) {
        this.errorModifier = errorModifier;
        return this;
    }

    /**
     * Build a configured instance of {@link PosthogClient}.
     *
     * @return a configured instance of {@link PosthogClient}
     */
    @NotNull
    public PosthogClient build() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL is required");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API Key is required");
        }

        var normalizedBaseUrl = baseUrl.endsWith("/")
            ? baseUrl.substring(0, baseUrl.length() - 1)
            : baseUrl;

        var client = new PosthogClient(normalizedBaseUrl, apiKey, serverFeatures);
        client.setRequestModifier(requestModifier);
        client.setDefaultResponseHandler(defaultResponseHandler);
        client.setErrorModifier(errorModifier);

        return client;
    }

}
