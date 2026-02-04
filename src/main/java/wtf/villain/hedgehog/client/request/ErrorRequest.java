package wtf.villain.hedgehog.client.request;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import wtf.villain.hedgehog.client.PosthogClient;
import wtf.villain.hedgehog.client.api.PosthogServerFeature;
import wtf.villain.hedgehog.data.error.Error;
import wtf.villain.hedgehog.data.error.StackFrameData;
import wtf.villain.hedgehog.data.event.Event;
import wtf.villain.hedgehog.data.person.Person;
import wtf.villain.hedgehog.util.Json;
import wtf.villain.hedgehog.util.JsonArrayBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface ErrorRequest {

    @ApiStatus.Internal
    @NotNull
    static CompletableFuture<Void> captureError(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull Error error) {
        var modifier = posthog.errorModifier();

        if (modifier != null) {
            var modified = modifier.modify(error);

            if (modified == null) {
                // discard error
                return CompletableFuture.completedFuture(null);
            }

            error = modified;
        }

        var event = buildEvent(posthog, error);
        return EventRequest.captureEvent(posthog, event, person);
    }

    @ApiStatus.Internal
    static void enqueueError(@NotNull PosthogClient posthog, @NotNull Person person, @NotNull Error error) {
        var modifier = posthog.errorModifier();

        if (modifier != null) {
            var modified = modifier.modify(error);

            if (modified == null) {
                // discard error
                return;
            }

            error = modified;
        }

        var event = buildEvent(posthog, error);
        EventRequest.enqueueEvent(posthog, event, person);
    }

    @NotNull
    private static Event buildEvent(@NotNull PosthogClient posthog, @NotNull Error error) {
        return Event.builder()
            .name("$exception")
            .properties(error.properties() == null ? Map.of() : error.properties())
            .property("$exception_list", Json.array()
                .use(array -> {
                    int depth = -1;

                    for (var throwable = error.throwable(); throwable != null; throwable = throwable.getCause()) {
                        array.add(buildThrowable(
                            posthog,
                            error,
                            throwable,
                            error.isHandled(),
                            error.isSynthetic(),
                            depth
                        ).build());

                        depth++;
                    }
                })
                .build())
            .build();
    }

    @NotNull
    private static Json buildThrowable(@NotNull PosthogClient posthog,
                                       @NotNull Error error,
                                       @NotNull Throwable throwable,
                                       boolean isHandled,
                                       boolean isSynthetic,
                                       int depth) {
        return Json.builder()
            .add("type", throwable.getClass().getSimpleName())
            .add("value", throwable.getMessage())

            .add("mechanism", Json.builder()
                .add("handled", isHandled)
                .add("synthetic", isSynthetic)
                .add("exception_id", depth + 1)
                .use(json -> {
                    if (depth != -1) {
                        json.add("type", "chained");
                        json.add("parent_id", depth);
                    } else {
                        json.add("type", "generic");
                    }
                })
            )

            .add("stacktrace", Json.builder()
                .add("type", "raw")
                .add("frames", buildFrames(posthog, error, throwable, throwable.getStackTrace()))
            );
    }

    @Nullable
    private static StackFrameData getStackFrameData(@NotNull PosthogClient posthog,
                                                    @NotNull Error error,
                                                    @NotNull Throwable throwable,
                                                    @NotNull StackTraceElement element) {
        var modifier = posthog.errorModifier();
        if (modifier != null) {
            return modifier.enrichStackFrame(error, throwable, element);
        }
        return null;
    }

    @NotNull
    private static JsonArrayBuilder buildFrames(@NotNull PosthogClient posthog,
                                                @NotNull Error error,
                                                @NotNull Throwable throwable,
                                                @UnknownNullability StackTraceElement @NotNull [] elements) {
        return Json.array()
            .use(array -> {
                for (int i = elements.length - 1; i >= 0; i--) {
                    var element = elements[i];
                    if (element == null) continue;

                    var data = getStackFrameData(posthog, error, throwable, element);

                    if (posthog.has(PosthogServerFeature.EXCEPTION_JAVA_FRAME)) {
                        array.add(Json.builder()
                            .add("platform", "java")
                            .use(json -> {
                                if (element.getFileName() != null && !element.getFileName().isBlank()) {
                                    json.add("filename", element.getFileName());
                                }

                                if (!element.getMethodName().isBlank()) {
                                    json.add("function", element.getMethodName());
                                }

                                if (element.getLineNumber() >= 0) {
                                    json.add("lineno", element.getLineNumber());
                                }

                                if (!element.getClassName().isBlank()) {
                                    // posthog naming is a bit misleading here, module is actually supposed to be the full class name
                                    json.add("module", element.getClassName());
                                }

                                if (data != null) {
                                    json.add("in_app", data.isInApp());
                                }

                                if (data != null && data.mappingSymbolId() != null && !data.mappingSymbolId().isBlank()) {
                                    json.add("map_id", data.mappingSymbolId());
                                }
                            })
                            .build());
                    } else {
                        // Fall back to the legacy "custom" frame type
                        array.add(Json.builder()
                            .add("platform", "custom")
                            .add("lang", "java")
                            .add("resolved", true)
                            .use(json -> {
                                if (element.getFileName() != null && !element.getFileName().isBlank()) {
                                    json.add("filename", element.getFileName());
                                }

                                if (!element.getMethodName().isBlank()) {
                                    json.add("function", element.getMethodName());
                                }

                                if (element.getLineNumber() >= 0) {
                                    json.add("lineno", element.getLineNumber());
                                }

                                if (!element.getClassName().isBlank()) {
                                    // posthog naming is a bit misleading here, module is actually supposed to be the full class name
                                    json.add("module", element.getClassName());
                                }

                                if (data != null) {
                                    json.add("in_app", data.isInApp());
                                }
                            })
                            .build());
                    }
                }
            });
    }

}
