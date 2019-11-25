package com.travelinsurancemaster.model.dto.cms.page;

import com.travelinsurancemaster.services.cms.IndexWhenPublishedInterceptor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Entity
@Indexed(interceptor = IndexWhenPublishedInterceptor.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Page extends BasePage {

}
