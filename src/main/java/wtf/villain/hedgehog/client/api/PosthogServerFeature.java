package wtf.villain.hedgehog.client.api;

/**
 * Features that the posthog server may or may not support.
 * <p>
 * Unfortunately, we can't auto-detect these easily, so the user has to specify them manually.
 */
@SuppressWarnings("unused")
public enum PosthogServerFeature {
    /**
     * Whether cymbal has support for the "java" frame type.
     * <p>
     * This was added in the commit: <a href="https://github.com/PostHog/posthog/commit/e9a620a27e92fd7b05f2808da1842f13442d385e">chore: add java lang to cymbal (#39404)</a>.
     * <p>
     * Without this feature, we're forced to use the "custom" frame type.
     * <p>
     * The "custom" frame type was added in the commit: <a href="https://github.com/PostHog/posthog/commit/074f03beea38a1199b50ab5c1aacddcd8582dc7f">feat(err): support custom platforms via CustomFrame (#33320)</a>.
     * <p>
     * Before that, cymbal only supported JS and Python frames.
     */
    EXCEPTION_JAVA_FRAME,
}
