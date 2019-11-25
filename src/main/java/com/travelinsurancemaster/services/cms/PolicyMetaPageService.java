package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaPackage;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.cms.page.*;
import com.travelinsurancemaster.repository.PolicyMetaPackageRepository;
import com.travelinsurancemaster.repository.cms.PolicyMetaPageRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 18.10.15.
 */
@Service
@Transactional(readOnly = true)
public class PolicyMetaPageService {
    private static final Logger log = LoggerFactory.getLogger(PolicyMetaPageService.class);
    public static final String TEMPLATE_PATH = "/cms/page/template/";

    @Autowired
    private PolicyMetaPageRepository policyMetaPageRepository;

    @Autowired
    private PageTypeService pageTypeService;

    @Autowired
    private FilingClaimContactService filingClaimContactService;

    @Autowired
    private PolicyMetaPackageRepository policyMetaPackageRepository;

    private EntityManager entityManager;

    @Autowired
    public PolicyMetaPageService(final EntityManagerFactory entityManagerFactory) { this.entityManager = entityManagerFactory.createEntityManager(); }

    @PostConstruct
    private void init() throws InterruptedException {
        Long count = policyMetaPageRepository.countByStatusAndDeletedFalse(PageStatus.PUBLISHED);
        int indexSize = search(null, -1).size();
        if (indexSize != count.intValue()) {
            log.info("rebuild index for Page {}:{}", count, indexSize);
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer(PolicyMetaPage.class).startAndWait();
        }
    }

    @Transactional(readOnly = true)
    public void fillCanBeDeletedForPolicyMetaCategoriesContent(PolicyMetaPage policyMetaPage){
        Set<Long> existedCategoryIds = policyMetaPage.getPolicyMeta().getPolicyMetaCategories().stream()
                .map(policyMetaCategory -> policyMetaCategory.getCategory().getId())
                .collect(Collectors.toSet());
        policyMetaPage.getPolicyMetaCategoryList()
                .forEach(policyMetaCategoryContent -> policyMetaCategoryContent.setCanDeleted(!existedCategoryIds.contains(policyMetaCategoryContent.getCategory().getId())));
        Set<Long> existedPackageIds = policyMetaPage.getPolicyMeta().getPolicyMetaPackages().stream()
                .map(PolicyMetaPackage::getId)
                .collect(Collectors.toSet());
        policyMetaPage.getPolicyMetaPackageList().stream()
                .filter(policyMetaCategoryContent -> policyMetaCategoryContent.getPackageOptions() != null)
                .forEach(policyMetaCategoryContent -> policyMetaCategoryContent.setCanDeleted(!existedPackageIds.contains(policyMetaCategoryContent.getPackageOptions().getId())));
    }

    public PolicyMetaPage getPolicyMetaPageWithContent(Long id) {
        PolicyMetaPage policyMetaPage = getPolicyMetaPage(id);
        if (policyMetaPage.isDeleted()) {
            return null;
        }
        return initContent(getPolicyMetaPage(id));
    }

    public PolicyMetaPage getPolicyMetaPage(Long id) {
        PolicyMetaPage policyMetaPage = policyMetaPageRepository.findOne(id);
        Hibernate.initialize(policyMetaPage.getVendorPage());
        return policyMetaPage;
    }

    public PolicyMetaPage getPolicyMetaPageWithContent(String name) {
        return initContent(getPolicyMetaPage(name));
    }

    private PolicyMetaPage initContent(PolicyMetaPage policyMetaPage) {
        if (policyMetaPage == null) {
            return null;
        }
        Hibernate.initialize(policyMetaPage.getVendorPage());
        policyMetaPage.getContent();
        policyMetaPage.getPolicyMetaCategoryList().forEach(policyMetaCategoryContent -> {
            policyMetaCategoryContent.getContent();
            policyMetaCategoryContent.getCertificateText();
        });
        policyMetaPage.getPolicyMetaCustomCategoryList().forEach(policyMetaCategoryContent -> {
            policyMetaCategoryContent.getContent();
            policyMetaCategoryContent.getCertificateText();
        });
        policyMetaPage.getPolicyMetaPlanInfoList().forEach(policyMetaCategoryContent -> {
            policyMetaCategoryContent.getContent();
            policyMetaCategoryContent.getCertificateText();
        });
        policyMetaPage.getPolicyMetaRestrictionsList().forEach(policyMetaCategoryContent -> {
            policyMetaCategoryContent.getContent();
            policyMetaCategoryContent.getCertificateText();
        });
        policyMetaPage.getPolicyMetaPackageList().forEach(policyMetaCategoryContent -> {
            policyMetaCategoryContent.getContent();
            policyMetaCategoryContent.getCertificateText();
        });
        return policyMetaPage;
    }

    public PolicyMetaPage getPolicyMetaPage(String name) {
        List<PolicyMetaPage> policyMetaPages = policyMetaPageRepository.findByNameAndDeletedFalse(name);
        if (CollectionUtils.isNotEmpty(policyMetaPages)) {
            return policyMetaPages.get(0);
        }
        return null;
    }

    public List<PolicyMetaPage> getPolicyMetaPageSorted() {
        return policyMetaPageRepository.findByStatusAndDeletedFalse(PageStatus.PUBLISHED, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPageByVendor(Vendor vendor) {
        return policyMetaPageRepository.findByVendorPageVendorAndDeletedFalse(vendor, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPackage> getPolicyMetaPackages(PolicyMeta policyMeta){
        return this.policyMetaPackageRepository.findByPolicyMeta(policyMeta);
    }

    public List<PolicyMetaPage> getPolicyMetaPagesByVendorPageSorted(VendorPage vendorPage) {
        return policyMetaPageRepository.findByVendorPageAndStatusAndDeletedFalse(vendorPage, PageStatus.PUBLISHED, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPagesByVendorPageSorted(VendorPage vendorPage) {
        return policyMetaPageRepository.findByVendorPageAndDeletedFalse(vendorPage, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPagesByVendorPageAndContactSorted(VendorPage vendorPage, FilingClaimContact contact) {
        return policyMetaPageRepository.findByVendorPageAndDeletedFalseAndFilingClaimContact(vendorPage, contact, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPagesByVendorPageSort(VendorPage vendorPage) {
        return policyMetaPageRepository.findByVendorPageAndDeletedFalse(vendorPage, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPagesByVendorPageFillingClaim(VendorPage vendorPage) {
        return policyMetaPageRepository.findByVendorPage(vendorPage, ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<PolicyMetaPage> getAllPolicyMetaPagesByVendorPage(VendorPage vendorPage) {
        return policyMetaPageRepository.findByVendorPageAndDeletedFalse(vendorPage);
    }

    public Long countPolicyMetaPages(VendorPage vendorPage) {
        return policyMetaPageRepository.countByVendorPageAndDeletedFalse(vendorPage);
    }

    public List<PolicyMetaPage> savePolicyMetaPageList(List<PolicyMetaPage> policyMetaPages) {
        List<PolicyMetaPage> savedPolicyMetaPages = new ArrayList<>();
        policyMetaPages.forEach(policyMetaPage -> { savedPolicyMetaPages.add(policyMetaPageRepository.saveAndFlush(policyMetaPage)); });
        return savedPolicyMetaPages;
    }

    @Transactional(readOnly = false)
    public PolicyMetaPage savePolicyMetaPage(PolicyMetaPage page) {
        PolicyMetaPage newPage;
        page.setModifiedDate(new Date());
        page.setPageType(pageTypeService.findAllSortedByName().get(0));
        String policyMetaPageName = TextUtils.generateCmsName(page.getCaption());
        if (page.getId() != null && (newPage = policyMetaPageRepository.findOne(page.getId())) != null) {
            newPage.setCaption(page.getCaption());
            newPage.setDescription(page.getDescription());
            newPage.setContent(page.getContent().replaceAll("(\r\n|\n)", ""));
            newPage.setModifiedDate(page.getModifiedDate());
            newPage.setPageType(page.getPageType());
            newPage.setStatus(page.getStatus());
            newPage.setVendorPage(page.getVendorPage());
            newPage.setName(policyMetaPageName);
            newPage.setFilingClaimContact(page.getFilingClaimContact());
            newPage.setPolicyMeta(page.getPolicyMeta());
            PolicyMetaPage newPolicyMetaPage = policyMetaPageRepository.saveAndFlush(newPage);

            return newPolicyMetaPage;
        }
        page.setName(policyMetaPageName);
        page.setCreateDate(new Date());
        page.setAuthor(SecurityHelper.getCurrentUser());
        PolicyMetaPage newPolicyMetaPage = policyMetaPageRepository.saveAndFlush(page);

        return newPolicyMetaPage;
    }

    public String getPageTemplatePath(Page page) {
        return TEMPLATE_PATH + page.getPageType().getTemplate();
    }

    public List<BasePage> search(String searchStr, int maxResults) {
        log.debug("search {}, limit {}", searchStr, maxResults);
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(PolicyMetaPage.class).get();

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

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, PolicyMetaPage.class)
                .setProjection("id", "name", "caption", "description", "content")
                .setResultTransformer(new AliasToBeanResultTransformer(PolicyMetaPage.class));

        if (maxResults > 0) {
            fullTextQuery.setMaxResults(maxResults);
        }

        @SuppressWarnings("unchecked")
        List<BasePage> pages = fullTextQuery.getResultList();

        log.debug("search result size {}", pages.size());
        return pages;
    }

    @Transactional(readOnly = false)
    public void deletePolicyMetaPage(Long id) {
        PolicyMetaPage policyMetaPage = getPolicyMetaPage(id);
        policyMetaPage.setDeleted(true);
        policyMetaPage.setStatus(PageStatus.DISABLED);
        policyMetaPage = savePolicyMetaPage(policyMetaPage);
        filingClaimContactService.removePolicyMetaFromFillingClaimContact(policyMetaPage);
    }

    @Deprecated
    public byte[] getCertificate(Long policyMetaPageId) {
        if (policyMetaPageId == null) {
            return null;
        }
        PolicyMetaPage policyMetaPage = getPolicyMetaPage(policyMetaPageId);
        if (policyMetaPage == null) {
            return null;
        }
        return policyMetaPage.getCertificate();
    }

    @Deprecated
    @Transactional(readOnly = false)
    public PolicyMetaPage updateCertificate(Long policyMetaPageId, byte[] bytes) {
        if (policyMetaPageId == null || bytes == null) {
            return null;
        }
        PolicyMetaPage policyMetaPage = getPolicyMetaPage(policyMetaPageId);
        if (policyMetaPage == null) {
            return null;
        }
        policyMetaPage.setCertificate(bytes);
        policyMetaPage.setCertificateModifiedDate(new Date());
//        vendorPageService.saveVendorPage(policyMetaPage.getVendorPage());
        return policyMetaPageRepository.saveAndFlush(policyMetaPage);
    }

    @Deprecated
    public boolean hasCertificate(Long id) {
        return policyMetaPageRepository.findByIdAndCertificateNotNull(id) != null;
    }
}
