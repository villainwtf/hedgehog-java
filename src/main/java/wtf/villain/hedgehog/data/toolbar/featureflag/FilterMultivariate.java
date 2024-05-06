package wtf.villain.hedgehog.data.toolbar.featureflag;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@SuppressWarnings("unused")
public class FilterMultivariate {

    private List<FilterMultivariateVariant> variants;
}
