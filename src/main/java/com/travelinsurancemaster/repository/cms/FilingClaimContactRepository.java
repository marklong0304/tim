package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.page.FilingClaimContact;
import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.repository.CacheableJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Chernov Artur on 04.12.15.
 */

@Repository
public interface FilingClaimContactRepository extends CacheableJpaRepository<FilingClaimContact, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimContact> findByIdAndDeletedFalse(Long id);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimContact> findAllByDeletedFalse(Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<FilingClaimContact> findAllByFilingClaimPageAndDeletedFalse(FilingClaimPage filingClaimPage, Sort sort);
}
