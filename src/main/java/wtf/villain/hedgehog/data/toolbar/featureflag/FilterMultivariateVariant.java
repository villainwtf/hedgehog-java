package wtf.villain.hedgehog.data.toolbar.featureflag;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@SuppressWarnings("unused")
public class FilterMultivariateVariant {

    private String key;
    private String name;

    @SerializedName("rollout_percentage")
    private int rolloutPercentage;
}
