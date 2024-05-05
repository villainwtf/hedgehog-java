package wtf.villain.hedgehog.data.earlyaccess;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@Getter
@RequiredArgsConstructor
public class EarlyAccessFeature {

    private final String id;
    private final String name;
    private final String description;
    private final String stage;

    @SerializedName("flagKey")
    private final String featureFlag;

    @ApiStatus.Internal // Gson
    public EarlyAccessFeature() {
        this(null, null, null, null, null);
    }
}
