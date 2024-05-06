package wtf.villain.hedgehog.data.toolbar.featureflag;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@SuppressWarnings("unused")
public class FeatureFlag {

    private int id;
    private int teamId;

    private String name;
    private String key;

    private Filters filters;

    private boolean deleted;
    private boolean active;
    @SerializedName("ensure_experience_continuity")
    private boolean ensureExperienceContinuity;
}
