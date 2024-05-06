package wtf.villain.hedgehog.data.toolbar.featureflag;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@SuppressWarnings("unused")
public class Filters {

    private List<FilterGroup> groups;
    private Map<String, String> payloads;

    @Nullable
    private FilterMultivariate multivariate;
}
