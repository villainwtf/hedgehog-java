package wtf.villain.hedgehog.data.person;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public class PropertyFilter {

    @NotNull
    public static PropertyFilter create() {
        return new PropertyFilter();
    }

    private boolean includePersonProperties;
    private boolean useSetSyntax;
    private boolean includeIp;
    private boolean includeFeatureFlags;

    @Contract("_ -> this")
    @NotNull
    public PropertyFilter includePersonProperties(boolean includePersonProperties) {
        this.includePersonProperties = includePersonProperties;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PropertyFilter useSetSyntax(boolean useSetSyntax) {
        this.useSetSyntax = useSetSyntax;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PropertyFilter includeIp(boolean includeIp) {
        this.includeIp = includeIp;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public PropertyFilter includeFeatureFlags(boolean includeFeatureFlags) {
        this.includeFeatureFlags = includeFeatureFlags;
        return this;
    }

}
