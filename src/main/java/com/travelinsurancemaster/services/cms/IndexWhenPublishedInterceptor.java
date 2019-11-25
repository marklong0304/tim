package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.cms.page.BasePage;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;

/**
 * Created by Alex on 08.08.2015.
 */
public class IndexWhenPublishedInterceptor implements EntityIndexingInterceptor<BasePage> {

    @Override
    public IndexingOverride onAdd(BasePage entity) {
        if (entity.getStatus() == PageStatus.PUBLISHED) {
            return IndexingOverride.APPLY_DEFAULT;
        }
        return IndexingOverride.SKIP;
    }

    @Override
    public IndexingOverride onUpdate(BasePage entity) {
        if (entity.getStatus() == PageStatus.PUBLISHED) {
            return IndexingOverride.APPLY_DEFAULT;
        }
        return IndexingOverride.REMOVE;
    }

    @Override
    public IndexingOverride onDelete(BasePage entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    @Override
    public IndexingOverride onCollectionUpdate(BasePage entity) {
        return onUpdate(entity);
    }
}
