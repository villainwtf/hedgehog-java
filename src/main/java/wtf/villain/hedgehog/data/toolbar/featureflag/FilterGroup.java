package wtf.villain.hedgehog.data.toolbar.featureflag;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@ToString
@SuppressWarnings("unused")
public class FilterGroup {

    @Nullable
    private String variant;

    private List<JsonElement> properties;

    @SerializedName("rollout_percentage")
    private int rolloutPercentage;
}
