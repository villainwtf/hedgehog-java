package wtf.villain.hedgehog.client.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.villain.hedgehog.data.error.Error;

@FunctionalInterface
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

}
