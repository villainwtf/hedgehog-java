package wtf.villain.hedgehog.client;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import wtf.villain.hedgehog.client.modifier.RequestModifier;

import java.util.Optional;

@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
public class PosthogClientBuilder {

    private Optional<String> baseUrl = Optional.empty();
    private Optional<String> apiKey = Optional.empty();
    private Optional<RequestModifier> requestModifier = Optional.empty();

    protected PosthogClientBuilder() {}

    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder baseUrl(@NotNull String baseUrl) {
        this.baseUrl = Optional.of(baseUrl);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder apiKey(@NotNull String apiKey) {
        this.apiKey = Optional.of(apiKey);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PosthogClientBuilder requestModifier(@NotNull RequestModifier requestModifier) {
        this.requestModifier = Optional.of(requestModifier);
        return this;
    }

    @NotNull
    public PosthogClient build() {
        var baseUrl = this.baseUrl.orElseThrow(() -> new IllegalStateException("Base URL is required"));
        var apiKey = this.apiKey.orElseThrow(() -> new IllegalStateException("API Key is required"));

        var client = new PosthogClient(baseUrl, apiKey);

        requestModifier.ifPresent(client::setRequestModifier);

        return client;
    }

}
