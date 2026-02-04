package wtf.villain.hedgehog.client.modifier;

import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
@SuppressWarnings("unused")
public interface RequestModifier {

    /**
     * Modifies the given HTTP request.
     *
     * @param request the original HTTP request
     * @return a modified Request.Builder, or null to cancel it
     */
    @Nullable
    Request.Builder modify(@NotNull Request.Builder request);

}
