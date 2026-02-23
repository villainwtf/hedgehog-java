package wtf.villain.hedgehog.client;

import com.google.gson.Gson;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.client.api.PosthogServerFeature;
import wtf.villain.hedgehog.client.modifier.ErrorModifier;
import wtf.villain.hedgehog.client.modifier.RequestModifier;
import wtf.villain.hedgehog.client.modifier.ResponseHandler;
import wtf.villain.hedgehog.client.request.*;
import wtf.villain.hedgehog.data.earlyaccess.EarlyAccessFeature;
import wtf.villain.hedgehog.data.error.Error;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.featureflag.FeatureFlagCollection;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.data.toolbar.ToolbarFeatureFlag;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Getter
public class PosthogClient {

    @NotNull
    public static PosthogClientBuilder builder() {
        return new PosthogClientBuilder();
    }

    private final String baseUrl;
    private final String apiKey;
    private final EnumSet<PosthogServerFeature> serverFeatures;

    private final Gson gson;

    private final QueueWorker queueWorker;

    private volatile RequestModifier requestModifier;
    private volatile ResponseHandler defaultResponseHandler;
    private volatile ErrorModifier errorModifier;

    protected PosthogClient(@NotNull String baseUrl,
                            @NotNull String apiKey,
                            @NotNull EnumSet<PosthogServerFeature> serverFeatures) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.serverFeatures = serverFeatures;

        this.gson = new Gson();

        this.queueWorker = new QueueWorker(this);
    }

    /**
     * Shuts down the client, waiting for queued events to be sent.
     */
    public void shutdown() {
        shutdown(true);
    }

    /**
     * Shuts down the client.
     *
     * @param await whether to wait for queued events to be sent before shutting down
     */
    public void shutdown(boolean await) {
        queueWorker.shutdown(await);
    }

    /**
     * Flushes the event queue, sending all queued events immediately.
     *
     * @param await whether to wait for the flush to complete
     */
    public void flush(boolean await) {
        queueWorker.flushQueue(await);
    }

    /**
     * Checks if the posthog server has a specific feature.
     *
     * @param feature the feature to check for
     * @return true if the posthog server has the feature, false otherwise
     */
    public boolean has(@NotNull PosthogServerFeature feature) {
        return serverFeatures.contains(feature);
    }

    /**
     * Sets a request modifier to modify requests before they are sent.
     *
     * @param requestModifier the request modifier, or null to remove it
     */
    public void setRequestModifier(@Nullable RequestModifier requestModifier) {
        this.requestModifier = requestModifier;
    }

    /**
     * Sets a default response handler to handle responses.
     *
     * @param defaultResponseHandler the response handler, or null to remove it
     */
    public void setDefaultResponseHandler(@Nullable ResponseHandler defaultResponseHandler) {
        this.defaultResponseHandler = defaultResponseHandler;
    }

    /**
     * Sets an error modifier to modify errors before they are sent.
     *
     * @param errorModifier the error modifier, or null to remove it
     */
    public void setErrorModifier(@Nullable ErrorModifier errorModifier) {
        this.errorModifier = errorModifier;
    }

    /**
     * Enqueues an event.
     *
     * @param event  the event to enqueue
     * @param person the person associated with the event
     */
    public void enqueueEvent(@NotNull Event event, @NotNull Person person) {
        EventRequest.enqueueEvent(this, event, person);
    }

    /**
     * Captures an event.
     *
     * @param event  the event to capture
     * @param person the person associated with the event
     * @return a {@link CompletableFuture} that completes when the event has been captured
     */
    @NotNull
    public CompletableFuture<Void> captureEvent(@NotNull Event event, @NotNull Person person) {
        return EventRequest.captureEvent(this, event, person);
    }

    /**
     * Evaluates feature flags for a person.
     *
     * @param person the person to evaluate feature flags for
     * @return a {@link CompletableFuture} that completes with the evaluated feature flags
     */
    @NotNull
    public CompletableFuture<FeatureFlagCollection> featureFlags(@NotNull Person person) {
        return FeatureFlagRequest.evaluateFeatureFlags(this, person)
            .thenApply(collection -> {
                person.storedFeatureFlags(collection);
                return collection;
            });
    }

    /**
     * Identifies a person.
     *
     * @param person the person to identify
     */
    public void identify(@NotNull Person person) {
        IdentifyRequest.identify(this, person);
    }

    /**
     * Enqueues a page view event.
     *
     * @param person the person associated with the page view
     * @param title  the title of the page
     */
    public void enqueuePageViewEvent(@NotNull Person person, @NotNull String title) {
        ViewRequest.pageView(this, person, title);
    }

    /**
     * Enqueues a screen view event.
     *
     * @param person     the person associated with the screen view
     * @param screenName the name of the screen
     */
    public void enqueueScreenViewEvent(@NotNull Person person, @NotNull String screenName) {
        ViewRequest.screenView(this, person, screenName);
    }

    /**
     * Captures an error.
     *
     * @param error  the error to capture
     * @param person the person associated with the error
     * @return a {@link CompletableFuture} that completes when the error has been captured
     */
    @NotNull
    public CompletableFuture<Void> captureError(@NotNull Error error, @NotNull Person person) {
        return ErrorRequest.captureError(this, person, error);
    }

    /**
     * Enqueues an error.
     *
     * @param error  the error to enqueue
     * @param person the person associated with the error
     */
    public void enqueueError(@NotNull Error error, @NotNull Person person) {
        ErrorRequest.enqueueError(this, person, error);
    }

    /**
     * Retrieves the list of early access features.
     *
     * @return a {@link CompletableFuture} that completes with the list of early access features
     */
    @NotNull
    public CompletableFuture<List<EarlyAccessFeature>> earlyAccessFeatures() {
        return EarlyAccessRequest.earlyAccessFeatures(this);
    }

    /**
     * Enqueues an early access feature enrollment change.
     *
     * @param person     the person whose enrollment is changing
     * @param feature    the feature to enroll or unenroll in
     * @param isEnrolled whether the person is enrolling (true) or unenrolling (false)
     */
    public void enqueueEarlyAccessFeatureEnrollment(@NotNull Person person, @NotNull String feature, boolean isEnrolled) {
        EarlyAccessRequest.enqueueEarlyAccessFeatureEnrollment(this, person, feature, isEnrolled);
    }

    /**
     * Updates an early access feature enrollment immediately.
     *
     * @param person     the person whose enrollment is changing
     * @param feature    the feature to enroll or unenroll in
     * @param isEnrolled whether the person is enrolling (true) or unenrolling (false)
     * @return a {@link CompletableFuture} that completes when the enrollment has been updated
     */
    @NotNull
    public CompletableFuture<Void> updateEarlyAccessFeatureEnrollmentImmediately(@NotNull Person person, @NotNull String feature, boolean isEnrolled) {
        return EarlyAccessRequest.updateEarlyAccessFeatureEnrollmentImmediately(this, person, feature, isEnrolled);
    }

    /**
     * Retrieves toolbar feature flags for the specified groups using a temporary token.
     *
     * @param groups         the groups to retrieve feature flags for
     * @param temporaryToken the temporary token to use for authentication
     * @return a {@link CompletableFuture} that completes with the list of toolbar feature flags
     */
    @NotNull
    public CompletableFuture<List<ToolbarFeatureFlag>> toolbarGetFeatureFlags(@NotNull String groups, @NotNull String temporaryToken) {
        return ToolbarRequest.toolbarGetFeatureFlags(this, groups, temporaryToken);
    }
}
