package wtf.villain.hedgehog.client.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.data.error.Error;
import wtf.villain.hedgehog.data.error.StackFrameData;

@SuppressWarnings("unused")
public interface ErrorModifier {

    /**
     * Modifies the given captured error.
     *
     * @param error the original captured error
     * @return a modified Error, or null to discard it
     */
    @Nullable
    Error modify(@NotNull Error error);

    /**
     * Enriches a stack frame element.
     *
     * @param error     the error being reported
     * @param throwable the current throwable
     * @param element   the stack trace element in question
     * @return a StackFrameData object with additional information
     */
    @Nullable
    StackFrameData enrichStackFrame(@NotNull Error error,
                                    @NotNull Throwable throwable,
                                    @NotNull StackTraceElement element);

}
