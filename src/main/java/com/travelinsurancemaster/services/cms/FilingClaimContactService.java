package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.cms.page.FilingClaimContact;
import com.travelinsurancemaster.model.dto.cms.page.FilingClaimPage;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.repository.cms.FilingClaimContactRepository;
import com.travelinsurancemaster.repository.cms.FilingClaimPageRepository;
import com.travelinsurancemaster.repository.cms.PolicyMetaPageRepository;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Chernov Artur on 15.10.15.
 */
@Service
@Transactional
public class FilingClaimContactService {
    private static final Logger log = LoggerFactory.getLogger(FilingClaimContactService.class);

    @Autowired
    private FilingClaimPageRepository filingClaimPageRepository;

    @Autowired
    private FilingClaimContactRepository filingClaimContactRepository;

    @Autowired
    private PolicyMetaPageRepository policyMetaPageRepository;

    @Autowired
    private PolicyMetaPageService policyMetaPageService;

    public FilingClaimContact getFilingClaimContact(Long id) {
        FilingClaimContact filingClaimContact = filingClaimContactRepository.findOne(id);
        if (filingClaimContact != null) {
            Hibernate.initialize(filingClaimContact.getPolicyMetaPages());
        }
        return filingClaimContact;
    }

    public List<FilingClaimContact> getFilingClaimContactsByPage(FilingClaimPage page) {
        List<FilingClaimContact> filingClaimContacts = filingClaimContactRepository.findAllByFilingClaimPageAndDeletedFalse(page, ServiceUtils.sortByFieldAscIgnoreCase("id"));
        return filingClaimContacts;
    }

    public void removePolicyMetaFromFillingClaimContact(PolicyMetaPage policyMetaPage) {
        if (policyMetaPage == null || policyMetaPage.getFilingClaimContact() == null) {
            return;
        }
        FilingClaimContact filingClaimContact = policyMetaPage.getFilingClaimContact();
        Iterator<PolicyMetaPage> iterator = filingClaimContact.getPolicyMetaPages().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(policyMetaPage.getId())) {
                iterator.remove();
                break;
            }
        }
        filingClaimContactRepository.save(filingClaimContact);
    }

    public FilingClaimContact saveFilingClaimContact(FilingClaimContact contact, VendorPage vendorPage, FilingClaimContact contact2) {
        FilingClaimContact newContact;
        String policyMetaPageIds[] = contact.getPolicyMetaPagesStr().split(",");
        Set<Long> policyMetaPagesHash = new HashSet<>();
        for (String id : policyMetaPageIds) {
            PolicyMetaPage policyMetaPage = policyMetaPageService.getPolicyMetaPage(NumberUtils.parseLong(id));
            contact.getPolicyMetaPages().add(policyMetaPage);
            policyMetaPagesHash.add(NumberUtils.parseLong(id));
        }
        contact.setModifiedDate(new Date());
        contact.setCustomerServiceNumber(StringUtils.EMPTY);
        contact.setCustomerServiceHoursOfOperation(StringUtils.EMPTY);
        contact.setClaimsFilingNumber(StringUtils.EMPTY);
        contact.setClaimsHoursOfOperation(StringUtils.EMPTY);
        contact.setWebsite(StringUtils.EMPTY);
        contact.setEmail(StringUtils.EMPTY);
        contact.setFax(StringUtils.EMPTY);
        contact.setTwentyFourHourEmergencyAssistanceNumbers(StringUtils.EMPTY);
        contact.setMailTo(StringUtils.EMPTY);
        if (contact.getId() != null && (newContact = filingClaimContactRepository.findOne(contact.getId())) != null) {
            newContact.setContent(contact.getContent().replaceAll("(\r\n|\n)", ""));
            newContact.setModifiedDate(contact.getModifiedDate());
            newContact.setFilingClaimPage(contact.getFilingClaimPage());
            newContact.setCustomerServiceNumber(contact.getCustomerServiceNumber());
            newContact.setCustomerServiceHoursOfOperation(contact.getCustomerServiceHoursOfOperation());
            newContact.setClaimsFilingNumber(contact.getClaimsFilingNumber());
            newContact.setClaimsHoursOfOperation(contact.getClaimsHoursOfOperation());
            newContact.setWebsite(contact.getWebsite());
            newContact.setEmail(contact.getEmail());
            newContact.setFax(contact.getFax());
            newContact.setTwentyFourHourEmergencyAssistanceNumbers(contact.getTwentyFourHourEmergencyAssistanceNumbers());
            newContact.setMailTo(contact.getMailTo());
            newContact.setDeleted(contact.isDeleted());
            newContact.setPolicyMetaPages(contact.getPolicyMetaPages());
//            List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(newContact.getFilingClaimPage().getVendorPage(), newContact);
//            policyMetaPages.addAll(policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(newContact.getFilingClaimPage().getVendorPage(), null));

            List<PolicyMetaPage> policyMetaPages = policyMetaPageService.getAllPolicyMetaPagesByVendorPageAndContactSorted(vendorPage, contact2);
            policyMetaPages.addAll(contact.getPolicyMetaPages());

            for (PolicyMetaPage policyMetaPage : policyMetaPages) {
                if (policyMetaPagesHash.contains(policyMetaPage.getId())) {
                    policyMetaPage.setFilingClaimContact(newContact);
                } else {
                    policyMetaPage.setFilingClaimContact(null);
                }
            }
            policyMetaPageService.savePolicyMetaPageList(policyMetaPages);
            return filingClaimContactRepository.save(newContact);
        }
        contact.setAuthor(SecurityHelper.getCurrentUser());
        contact.setCreateDate(new Date());
        FilingClaimContact filingClaimContact = filingClaimContactRepository.save(contact);
        for (PolicyMetaPage policyMetaPage : contact.getPolicyMetaPages()) {
            policyMetaPage.setFilingClaimContact(contact);
            policyMetaPageService.savePolicyMetaPage(policyMetaPage);
        }
        return filingClaimContact;
    }

    public void deleteFilingClaimContact(Long filingClaimContactId) {
        FilingClaimContact filingClaimContact = getFilingClaimContact(filingClaimContactId);
        if (filingClaimContact == null) {
            return;
        }
        List<PolicyMetaPage> policyMetaPages = filingClaimContact.getPolicyMetaPages();
        for (PolicyMetaPage policyMetaPage : policyMetaPages) {
            policyMetaPage.setFilingClaimContact(null);
        }
        policyMetaPageRepository.save(policyMetaPages);
        filingClaimContact.getPolicyMetaPages().clear();
        filingClaimContact.setDeleted(true);
        filingClaimContactRepository.save(filingClaimContact);
    }
}
