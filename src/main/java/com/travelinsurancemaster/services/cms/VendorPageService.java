package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.*;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.cms.VendorPageRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import com.travelinsurancemaster.util.TextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.Hibernate;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;

/**
 * Created by Chernov Artur on 15.10.15.
 */
@Service
@Transactional(readOnly = true)
public class VendorPageService {
    private static final Logger log = LoggerFactory.getLogger(VendorPageService.class);

    @Autowired
    private VendorPageRepository vendorPageRepository;

    @Autowired
    private PageTypeService pageTypeService;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private FilingClaimPageService filingClaimPageService;

    private EntityManager entityManager;

    @Autowired
    public VendorPageService(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @PostConstruct
    private void init() throws InterruptedException {
        Long count = vendorPageRepository.countByStatusAndDeletedFalse(PageStatus.PUBLISHED);
        int indexSize = search(null, -1).size();
        if (indexSize != count.intValue()) {
            log.info("rebuild index for Page {}:{}", count, indexSize);
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer(Page.class).startAndWait();
        }
    }

    public List<VendorPage> findAllSortedByName() {
        return vendorPageRepository.findAll(ServiceUtils.sortByFieldAscIgnoreCase("name"));
    }

    public VendorPage getVendorPage(Long id) {
        if (id == null) {
            return null;
        }
        VendorPage vendorPage = vendorPageRepository.findOne(id);
        if (vendorPage != null) {
            Hibernate.initialize(vendorPage.getPolicyMetaPages());
        }
        return vendorPage;
    }

    public VendorPage getVendorPageWithContent(String name) {
        VendorPage vendorPage = getVendorPage(name);
        if (vendorPage == null) {
            return null;
        }
        vendorPage.getContent();
        return vendorPage;
    }

    public VendorPage getVendorPage(String name) {
        List<VendorPage> vendorPages = vendorPageRepository.findByNameAndDeletedFalse(name);
        if (CollectionUtils.isNotEmpty(vendorPages)) {
            VendorPage vendorPage = vendorPages.get(0);
            Hibernate.initialize(vendorPage.getPolicyMetaPages());
            return vendorPage;
        }
        return null;
    }

    public VendorPage getPublishedVendorPageByVendor(Vendor vendor) {
        List<VendorPage> vendorPages = vendorPageRepository.findByVendorAndStatusAndDeletedFalse(vendor, PageStatus.PUBLISHED);
        return CollectionUtils.isNotEmpty(vendorPages) ? vendorPages.get(0) : null;
    }

    public VendorPage getVendorPageByVendor(Vendor vendor) {
        List<VendorPage> vendorPages = vendorPageRepository.findByVendorAndDeletedFalse(vendor);
        if (CollectionUtils.isNotEmpty(vendorPages)) {
            VendorPage vendorPage = vendorPages.get(0);
            Hibernate.initialize(vendorPage.getPolicyMetaPages());
            return vendorPage;
        }
        return null;
    }

    public List<VendorPage> findAllSorted() {
        List<VendorPage> vendorPages = vendorPageRepository.findAllByDeletedFalseAndDeletedDateIsNull(ServiceUtils.sortByFieldAscIgnoreCase("caption"));
        for (VendorPage vendorPage : vendorPages) {
            Hibernate.initialize(vendorPage.getPolicyMetaPages());
        }
        return vendorPages;
    }

    @Transactional(readOnly = false)
    public VendorPage saveVendorPage(VendorPage page) {
        VendorPage newPage;
        page.setModifiedDate(new Date());
        page.setPageType(pageTypeService.findAllSortedByName().get(0));
        String vendorPageName = TextUtils.generateCmsName(page.getCaption());
        if (page.getId() != null && (newPage = vendorPageRepository.findOne(page.getId())) != null) {
            newPage.setCaption(page.getCaption());
            newPage.setDescription(page.getDescription());
            newPage.setContent(page.getContent().replaceAll("(\r\n|\n)", ""));
            newPage.setModifiedDate(page.getModifiedDate());
            newPage.setPageType(page.getPageType());
            newPage.setStatus(page.getStatus());
            newPage.setVendor(page.getVendor());
            newPage.setRating(page.getRating());
            newPage.setName(vendorPageName);
            newPage.setDeletedDate(page.getDeletedDate());
            return vendorPageRepository.save(newPage);
        }
        page.setName(vendorPageName);
        page.setCreateDate(new Date());
        page.setAuthor(SecurityHelper.getCurrentUser());
        return vendorPageRepository.save(page);
    }

    public List<BasePage> search(String searchStr, int maxResults) {
        log.debug("search {}, limit {}", searchStr, maxResults);
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(VendorPage.class).get();

        Query query;
        if (StringUtils.isBlank(searchStr)) {
            query = queryBuilder.all().createQuery();
        } else {

            query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1)
                    .onField("name")
                    .andField("caption").boostedTo(3)
                    .andField("description")
                    .andField("content").boostedTo(2)
                    .matching(searchStr.trim().toLowerCase()).createQuery();
        }

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, VendorPage.class)
                .setProjection("id", "name", "caption", "description", "content")
                .setResultTransformer(new AliasToBeanResultTransformer(VendorPage.class));

        if (maxResults > 0) {
            fullTextQuery.setMaxResults(maxResults);
        }

        @SuppressWarnings("unchecked")
        List<BasePage> pages = fullTextQuery.getResultList();

        log.debug("search result size {}", pages.size());
        return pages;
    }

    @Transactional(readOnly = false)
    public void deleteVendorPage(Long vendorPageId) {
        if (vendorPageId == null) {
            return;
        }
        VendorPage vendorPage = getVendorPage(vendorPageId);
        if (vendorPage == null) {
            return;
        }
        vendorPage.setDeleted(true);
        vendorPage.setStatus(PageStatus.DISABLED);
        vendorPage = saveVendorPage(vendorPage);
        List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPage(vendorPage);
        for (PolicyMetaPage policyMetaPage : policyMetaPages) {
            policyMetaPageService.deletePolicyMetaPage(policyMetaPage.getId());
        }
        if (vendorPage.getFilingClaimPage() != null) {
            filingClaimPageService.deleteFilingClaimPage(vendorPage.getFilingClaimPage().getId());
        }
    }

    public Map<VendorPage, List<PolicyMetaPage>> getVendorPagesMap(User user) {
        Map<VendorPage, List<PolicyMetaPage>> vendorPagesMap = new LinkedHashMap<>();
        List<PolicyMetaPage> policyMetaPageSorted = policyMetaPageService.getPolicyMetaPageSorted();
        for (PolicyMetaPage policyMetaPage : policyMetaPageSorted) {
            VendorPage vendorPage = policyMetaPage.getVendorPage();
            if (!vendorPage.getStatus().equals(PageStatus.PUBLISHED)) {
                continue;
            }
            if (vendorPage.getVendor().isTest()) {
                if(user == null || !user.getRoles().contains(Role.ROLE_ADMIN) && !vendorPage.getVendor().getLongTestUserIds().contains(user.getId())) {
                    continue;
                }
            }
            if (vendorPage.getVendor().isShowPureConsumers() && (user == null || user.getRoles().size() == 0 || user.getRoles().size() == 1 && user.getRoles().contains(Role.ROLE_USER))) {
                continue;
            }
            Hibernate.initialize(vendorPage);
            if (!vendorPagesMap.containsKey(vendorPage)) {
                if(!policyMetaPage.getPolicyMeta().getVendor().isActive()) continue;
                vendorPagesMap.put(vendorPage, new ArrayList<>());
            }
            vendorPagesMap.get(vendorPage).add(policyMetaPage);
        }
        return vendorPagesMap;
    }

    public VendorPage getById(Long id) {
        return vendorPageRepository.getOne(id);
    }
}
