package com.travelinsurancemaster.util.bootstrapmultiselectdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 11/25/15.
 */
public class BootstrapMultiselectDataProviders {

    private List<BootstrapMultiselectDataProvider> bootstrapMultiselectDataProvidersList = new ArrayList<>();

    /**
     * @return preprocessed data for the bootstrap multiselect (some values can be checked by default)
     */
    public List<BootstrapMultiselectDataProvider> getBootstrapMultiselectDataProvidersList() {
        return bootstrapMultiselectDataProvidersList;
    }

    public void setBootstrapMultiselectDataProvidersList(List<BootstrapMultiselectDataProvider> bootstrapMultiselectDataProvidersList) {
        this.bootstrapMultiselectDataProvidersList = bootstrapMultiselectDataProvidersList;
    }
}
