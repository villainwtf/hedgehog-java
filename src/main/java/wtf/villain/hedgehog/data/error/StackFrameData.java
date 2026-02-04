package wtf.villain.hedgehog.data.error;

import org.jetbrains.annotations.Nullable;

/**
 * Data about a stack frame in an error/exception.
 *
 * @param isInApp         whether this frame is considered "in app" code
 * @param mappingSymbolId the mapping symbol id for this frame, if any
 */
public record StackFrameData(boolean isInApp,
                             @Nullable String mappingSymbolId) {
}
