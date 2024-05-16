package wtf.villain.hedgehog.client.modifier;

import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
@SuppressWarnings("unused")
public interface RequestModifier {

    @Nullable
    Request.Builder modify(@NotNull Request.Builder request);

}
