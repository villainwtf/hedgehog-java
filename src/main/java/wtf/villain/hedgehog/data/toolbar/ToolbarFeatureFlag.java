package wtf.villain.hedgehog.data.toolbar;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import wtf.villain.hedgehog.data.toolbar.featureflag.FeatureFlag;

@Getter
@ToString
@SuppressWarnings("unused")
public class ToolbarFeatureFlag {

    @SerializedName("feature_flag")
    private FeatureFlag featureFlag;

    @SerializedName("value")
    private boolean active;
}
