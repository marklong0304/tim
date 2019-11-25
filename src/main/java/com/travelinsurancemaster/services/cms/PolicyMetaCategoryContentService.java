package com.travelinsurancemaster.services.cms;


import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaPackage;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.repository.cms.PolicyMetaCategoryContentRepository;
import com.travelinsurancemaster.services.CategoryService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.bootstrapmultiselectdata.BootstrapMultiselectDataProvider;
import com.travelinsurancemaster.util.bootstrapmultiselectdata.BootstrapMultiselectDataProviders;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Created by Chernov Artur on 10.20.15.
 */

@Service
@Transactional(readOnly = true)
public class PolicyMetaCategoryContentService {

    private static final Logger log = LoggerFactory.getLogger(PolicyMetaCategoryContentService.class);

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMetaCategoryContentRepository policyMetaCategoryContentRepository;

    public PolicyMetaCategoryContent getPolicyMetaCategoryContentCertificate(Long id) {
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentRepository.findOne(id);
        if (policyMetaCategoryContent == null) {
            return null;
        }
        policyMetaCategoryContent.getCertificateText();
        return policyMetaCategoryContent;
    }

    public PolicyMetaCategoryContent getPolicyMetaCategoryContent(Long id) {
        PolicyMetaCategoryContent policyMetaCategoryContent = policyMetaCategoryContentRepository.findOne(id);
        if (policyMetaCategoryContent == null) {
            return null;
        }
        policyMetaCategoryContent.getContent();
        policyMetaCategoryContent.getCertificateText();
        Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForCategory());
        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForCategory().getVendorPage());
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForCategory().getVendorPage().getPolicyMetaPages());
        }
        Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForCustomCategory());
        if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForCustomCategory().getVendorPage());
        }
        Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForPlanInfo());
        if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForPlanInfo().getVendorPage());
        }
        Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForRestrictions());
        if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null) {
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPageForRestrictions().getVendorPage());
        }
        Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPackage());
        if (policyMetaCategoryContent.getPolicyMetaPackage() != null) {
            Hibernate.initialize(policyMetaCategoryContent.getPolicyMetaPackage().getVendorPage());
            Hibernate.initialize(policyMetaCategoryContent.getPackageOptions());
        }
        Hibernate.initialize(policyMetaCategoryContent.getCategory());
        Hibernate.initialize(policyMetaCategoryContent.getCertificateText());
        return policyMetaCategoryContent;
    }

    public String prepareMultiselectData(PolicyMetaPage policyMetaPageCurrent) {
        BootstrapMultiselectDataProviders bootstrapMultiselectDataProviders = new BootstrapMultiselectDataProviders();
        for (PolicyMetaPage policyMetaPage : getPolicyMetaPagesInitialized(policyMetaPageCurrent)) {
            if (policyMetaPageCurrent.getName().equals(policyMetaPage.getName())) {
                bootstrapMultiselectDataProviders.getBootstrapMultiselectDataProvidersList().add(
                        new BootstrapMultiselectDataProvider(policyMetaPage.getCaption() + " (" + policyMetaPage.getName() + ")",
                                policyMetaPage.getName(), true, true));
            } else {
                bootstrapMultiselectDataProviders.getBootstrapMultiselectDataProvidersList().add(
                        new BootstrapMultiselectDataProvider(policyMetaPage.getCaption() + " (" + policyMetaPage.getName() + ")",
                                policyMetaPage.getName()));
            }
        }
        return JsonUtils.getJsonString(bootstrapMultiselectDataProviders);
    }

    public PolicyMetaCategoryContent getByCategoryIdAndPolicyMetaPageId(Long categoryId, Long policyMetaPageId) {
        return policyMetaCategoryContentRepository.findByCategoryIdAndPolicyMetaPageForCategoryIdAndDeletedFalse(categoryId, policyMetaPageId);
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaCategoryContentList() {
        return policyMetaCategoryContentRepository.findAllByDeletedFalse();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaCategoryContent savePolicyMetaCategoryContent(PolicyMetaCategoryContent policyMetaCategoryContent) {
        PolicyMetaCategoryContent newPolicyMetaCategoryContent;
        policyMetaCategoryContent.setModifiedDate(new Date());
        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaCategoryContent.getPolicyMetaPageForCategory().getId());
            policyMetaCategoryContent.setPolicyMetaPageForCategory(policyMetaPage);
            Category category = categoryService.get(policyMetaCategoryContent.getCategory().getId());
            policyMetaCategoryContent.setCategory(category);
        }
        if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaCategoryContent.getPolicyMetaPageForCustomCategory().getId());
            policyMetaCategoryContent.setPolicyMetaPageForCustomCategory(policyMetaPage);
        }
        if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaCategoryContent.getPolicyMetaPageForPlanInfo().getId());
            policyMetaCategoryContent.setPolicyMetaPageForPlanInfo(policyMetaPage);
        }
        if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaCategoryContent.getPolicyMetaPageForRestrictions().getId());
            policyMetaCategoryContent.setPolicyMetaPageForRestrictions(policyMetaPage);
        }
        if (policyMetaCategoryContent.getPolicyMetaPackage() != null) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(policyMetaCategoryContent.getPolicyMetaPackage().getId());
            policyMetaCategoryContent.setPolicyMetaPackage(policyMetaPage);
        }
        if (policyMetaCategoryContent.getContent() == null) {
            policyMetaCategoryContent.setContent("");
        }
        if (policyMetaCategoryContent.getCertificateText() == null) {
            policyMetaCategoryContent.setCertificateText("");
        }

        if (policyMetaCategoryContent.getId() != null && (newPolicyMetaCategoryContent = policyMetaCategoryContentRepository.findOne(policyMetaCategoryContent.getId())) != null) {
            newPolicyMetaCategoryContent.setContent(policyMetaCategoryContent.getContent().replaceAll("(\r\n|\n)", ""));
            newPolicyMetaCategoryContent.setModifiedDate(policyMetaCategoryContent.getModifiedDate());
            newPolicyMetaCategoryContent.setPolicyMetaPageForCategory(policyMetaCategoryContent.getPolicyMetaPageForCategory());
            newPolicyMetaCategoryContent.setPolicyMetaPageForCustomCategory(policyMetaCategoryContent.getPolicyMetaPageForCustomCategory());
            newPolicyMetaCategoryContent.setPolicyMetaPageForPlanInfo(policyMetaCategoryContent.getPolicyMetaPageForPlanInfo());
            newPolicyMetaCategoryContent.setPolicyMetaPageForRestrictions(policyMetaCategoryContent.getPolicyMetaPageForRestrictions());
            newPolicyMetaCategoryContent.setPolicyMetaPackage(policyMetaCategoryContent.getPolicyMetaPackage());
            newPolicyMetaCategoryContent.setPackageOptions(policyMetaCategoryContent.getPackageOptions());
            newPolicyMetaCategoryContent.setCategory(policyMetaCategoryContent.getCategory());
            newPolicyMetaCategoryContent.setName(policyMetaCategoryContent.getName());
            newPolicyMetaCategoryContent.setGroup(policyMetaCategoryContent.getGroup());
            newPolicyMetaCategoryContent.setCertificateText(policyMetaCategoryContent.getCertificateText());
            return policyMetaCategoryContentRepository.save(newPolicyMetaCategoryContent);
        }
        policyMetaCategoryContent.setCreateDate(new Date());
        policyMetaCategoryContent.setAuthor(SecurityHelper.getCurrentUser());
        return policyMetaCategoryContentRepository.save(policyMetaCategoryContent);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaCategoryContent save(PolicyMetaCategoryContent policyMetaCategoryContent) {
        policyMetaCategoryContent.setModifiedDate(new Date());
        return policyMetaCategoryContentRepository.saveAndFlush(policyMetaCategoryContent);
    }

    @Transactional(readOnly = false)
    public void deletePolicyMetaCategoryContent(Long id) {
        PolicyMetaCategoryContent deletedPolicyMetaCategoryContent = policyMetaCategoryContentRepository.findOne(id);
        policyMetaPageService.savePolicyMetaPage(getPolicyMetaPage(deletedPolicyMetaCategoryContent));
        policyMetaCategoryContentRepository.delete(id);
    }

    public PolicyMetaPage getPolicyMetaPage(PolicyMetaCategoryContent policyMetaCategoryContent) {
        return policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null ? policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() :
                policyMetaCategoryContent.getPolicyMetaPageForCategory() != null ? policyMetaCategoryContent.getPolicyMetaPageForCategory() :
                        policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null ? policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() :
                                policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null ? policyMetaCategoryContent.getPolicyMetaPageForRestrictions() :
                                        policyMetaCategoryContent.getPolicyMetaPackage();
    }

    @Transactional(readOnly = false)
    public PolicyMetaCategoryContent saveAllPolicyMetaCategoryContent(PolicyMetaCategoryContent policyMetaCategoryContent, boolean saveContent, boolean saveText) {
        PolicyMetaCategoryContent policyMetaCategoryContentSaved = savePolicyMetaCategoryContent(policyMetaCategoryContent);
        Set<String> plansSelected = policyMetaCategoryContent.getPlansSelected();
        if (plansSelected == null) {
            return policyMetaCategoryContentSaved;
        }
        for (String planName : plansSelected) {
            saveOrCreatePolicyMetaCategoryContent(policyMetaCategoryContent, policyMetaCategoryContentSaved, planName, saveContent, saveText);
        }
        return policyMetaCategoryContentSaved;
    }

    private void saveOrCreatePolicyMetaCategoryContent(PolicyMetaCategoryContent policyMetaCategoryContent,
                                                       PolicyMetaCategoryContent policyMetaCategoryContentSaved,
                                                       String planName,
                                                       boolean saveContent, boolean saveText) {
        PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(planName);
        if (policyMetaPage == null) {
            return;
        }

        PolicyMetaCategoryContent policyMetaCategoryContentToUpdate;
        if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
            String categoryCode = policyMetaCategoryContentSaved.getCategory().getCode();
            policyMetaCategoryContentToUpdate = getPolicyMetaCategoryContentToUpdateByCode(policyMetaPage, categoryCode);
        } else {
            String cmsCategoryName = policyMetaCategoryContent.getName();
            if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
                policyMetaCategoryContentToUpdate = policyMetaCategoryContentRepository.findByPolicyMetaPageForPlanInfoAndNameAndDeletedFalse(policyMetaPage, cmsCategoryName).stream().findAny().orElse(null);
            } else if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null){
                policyMetaCategoryContentToUpdate = policyMetaCategoryContentRepository.findByPolicyMetaPageForCustomCategoryAndNameAndDeletedFalse(policyMetaPage, cmsCategoryName).stream().findAny().orElse(null);
            } else {
                policyMetaCategoryContentToUpdate = policyMetaCategoryContentRepository.findByPolicyMetaPageForRestrictionsAndNameAndDeletedFalse(policyMetaPage, cmsCategoryName).stream().findAny().orElse(null);
            }
        }

        if (policyMetaCategoryContentToUpdate != null &&
                policyMetaCategoryContentToUpdate.getId().equals(policyMetaCategoryContentSaved.getId())) {
            return;
        }
        if (policyMetaCategoryContentToUpdate == null) {
            policyMetaCategoryContentToUpdate = new PolicyMetaCategoryContent();
            policyMetaCategoryContentToUpdate.setCategory(policyMetaCategoryContent.getCategory());
            policyMetaCategoryContentToUpdate.setName(policyMetaCategoryContent.getName());
            policyMetaCategoryContentToUpdate.setGroup(policyMetaCategoryContent.getGroup());
            if (policyMetaCategoryContent.getPolicyMetaPageForCategory() != null) {
                policyMetaCategoryContentToUpdate.setPolicyMetaPageForCategory(policyMetaPage);
            }
            if (policyMetaCategoryContent.getPolicyMetaPageForCustomCategory() != null) {
                policyMetaCategoryContentToUpdate.setPolicyMetaPageForCustomCategory(policyMetaPage);
            }
            if (policyMetaCategoryContent.getPolicyMetaPageForPlanInfo() != null) {
                policyMetaCategoryContentToUpdate.setPolicyMetaPageForPlanInfo(policyMetaPage);
            }
            if (policyMetaCategoryContent.getPolicyMetaPageForRestrictions() != null) {
                policyMetaCategoryContentToUpdate.setPolicyMetaPageForRestrictions(policyMetaPage);
            }
            if (policyMetaCategoryContent.getPolicyMetaPackage() != null) {
                policyMetaCategoryContentToUpdate.setPolicyMetaPackage(policyMetaPage);
            }
        }
        if (saveContent) {
            policyMetaCategoryContentToUpdate.setContent(policyMetaCategoryContent.getContent().replaceAll("(\r\n|\n)", ""));
        }
        if (saveText) {
            policyMetaCategoryContentToUpdate.setCertificateText(policyMetaCategoryContent.getCertificateText());
        }
        savePolicyMetaCategoryContent(policyMetaCategoryContentToUpdate);
    }

    private PolicyMetaCategoryContent getPolicyMetaCategoryContentToUpdateByCode(PolicyMetaPage policyMetaPage, String categoryCode) {
        List<PolicyMetaCategoryContent> existedPolicyMetaCategoryContentList =  policyMetaCategoryContentRepository.findAllByPolicyMetaPageForCategoryAndDeletedFalse(policyMetaPage);
        return existedPolicyMetaCategoryContentList.stream()
                .filter(pmcc -> StringUtils.equals(pmcc.getCategory().getCode(), categoryCode)).findAny().orElse(null);
    }

    public List<PolicyMetaPage> getPolicyMetaPagesInitialized(PolicyMetaPage policyMetaPage) {
        if (policyMetaPage == null) {
            return Collections.emptyList();
        }
        return policyMetaPageService.getAllPolicyMetaPagesByVendorPageSorted(policyMetaPage.getVendorPage());
    }

    public PolicyMetaCategoryContent getPolicyMetaCategoryContent(PolicyMeta policyMeta, Category category) {
        PolicyMetaPage policyMetaPage = policyMeta.getPolicyMetaPage();
        List<PolicyMetaCategoryContent> policyMetaCategoryContentList = policyMetaPage.getPolicyMetaCategoryList();
        Optional<PolicyMetaCategoryContent> policyMetaCategoryContentOptional = policyMetaCategoryContentList.stream().filter(pmcc -> pmcc.getCategory().getId().equals(category.getId())).findFirst();
        return policyMetaCategoryContentOptional.isPresent() ? policyMetaCategoryContentOptional.get() : null;
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaCustomCategoryContentByName(String name, Long policyMetaPageId) {
        return policyMetaCategoryContentRepository.findAllByNameAndDeletedFalseAndPolicyMetaPageForCustomCategoryId(name, policyMetaPageId);
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaPlanInfoContentByName(String name, Long policyMetaPageId) {
        return policyMetaCategoryContentRepository.findAllByNameAndDeletedFalseAndPolicyMetaPageForPlanInfoId(name, policyMetaPageId);
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaRestrictionsContentByName(String name, Long policyMetaPageId) {
        return policyMetaCategoryContentRepository.findAllByNameAndDeletedFalseAndPolicyMetaPageForRestrictionsId(name, policyMetaPageId);
    }

    public PolicyMetaCategoryContent getPolicyMetaPackageById(Long policyMetaPackage) {
        return policyMetaCategoryContentRepository.findByPackageOptionsIdAndDeletedFalse(policyMetaPackage);
    }

    public List<PolicyMetaCategoryContent> getPolicyMetaPackageContentListByName(String name, Long policyMetaPageId) {
        return policyMetaCategoryContentRepository.findAllByNameAndDeletedFalseAndPolicyMetaPackageId(name, policyMetaPageId);
    }

    @Transactional
    public void savePackegeForContent(PolicyMetaPackage policyMetaPackage) {
        PolicyMetaCategoryContent policyMetaCategoryContentExemp = new PolicyMetaCategoryContent();
        PolicyMeta policyMetaById = policyMetaService.getPolicyMetaById(policyMetaPackage.getPolicyMeta().getId());
        policyMetaCategoryContentExemp.setPolicyMetaPackage(policyMetaPageService.getPolicyMetaPage(policyMetaById.getPolicyMetaPage().getId()));
        policyMetaCategoryContentExemp.setPackageOptions(policyMetaPackage);
        policyMetaCategoryContentExemp.setName(policyMetaPackage.getName());
        PolicyMetaCategoryContent policyMetaPackageById = getPolicyMetaPackageById(policyMetaPackage.getId());
        if (policyMetaPackageById == null) {
            savePolicyMetaCategoryContent(policyMetaCategoryContentExemp);
        }
    }

    @Transactional
    public void createNewPolicyMetaCategoryContentIfNotExists(PolicyMeta policyMeta, PolicyMetaCategory policyMetaCategory){
        if (policyMeta.getPolicyMetaPage() == null){
            log.debug("PolicyMetaPage for PolicyMeta not found. PolicyMeta={}", policyMeta);
            throw new EntityNotFoundException("PolicyMetaPage not found");
        }
        List<PolicyMetaCategoryContent> existedPolicyMetaCategoryContentList =  policyMetaCategoryContentRepository.findAllByPolicyMetaPageForCategoryAndDeletedFalse(policyMeta.getPolicyMetaPage());
        boolean policyMetaCategoryContentIsNotExists = existedPolicyMetaCategoryContentList.stream().noneMatch(pmcc -> Objects.equals(pmcc.getCategory(), policyMetaCategory.getCategory()));
        if (policyMetaCategoryContentIsNotExists){
            log.debug("Creating PolicyMetaCategoryContent for PolicyMetaCategory={}", policyMetaCategory);
            PolicyMetaCategoryContent policyMetaCategoryContent = new PolicyMetaCategoryContent(policyMeta, policyMetaCategory);
            savePolicyMetaCategoryContent(policyMetaCategoryContent);
        }
    }
}
