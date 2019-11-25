package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.cms.page.*;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.cms.FilingClaimPageRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import com.travelinsurancemaster.util.TextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chernov Artur on 15.10.15.
 */
@Service
@Transactional
public class FilingClaimPageService {
    private static final Logger log = LoggerFactory.getLogger(FilingClaimPageService.class);
    public static final String TEMPLATE_PATH = "/cms/page/template/";

    @Autowired
    private FilingClaimPageRepository filingClaimPageRepository;

    @Autowired
    private FilingClaimContactService filingClaimContactService;

    @Autowired
    private PageTypeService pageTypeService;

    public FilingClaimPage getFilingClaimPage(Long id) {
        FilingClaimPage filingClaimPage = filingClaimPageRepository.findOne(id);
        if (filingClaimPage != null) {
            Hibernate.initialize(filingClaimPage.getFilingClaimContacts());
            Hibernate.initialize(filingClaimPage.getVendorPage());
        }
        return filingClaimPage;
    }

    public FilingClaimPage getFilingClaimPage(String name) {
        List<FilingClaimPage> filingClaimPageList = filingClaimPageRepository.findByNameAndDeletedFalse(name);
        if (CollectionUtils.isNotEmpty(filingClaimPageList)) {
            FilingClaimPage filingClaimPage = filingClaimPageList.get(0);
            Hibernate.initialize(filingClaimPage.getFilingClaimContacts());
            return filingClaimPage;
        }
        return null;
    }

    public FilingClaimPage getFilingClaimPageByVendorPage(VendorPage vendorPage) {
        List<FilingClaimPage> filingClaimPages = filingClaimPageRepository.findByVendorPageAndDeletedFalse(vendorPage);
        if (CollectionUtils.isNotEmpty(filingClaimPages)) {
            FilingClaimPage filingClaimPage = filingClaimPages.get(0);
            Hibernate.initialize(filingClaimPage.getFilingClaimContacts());
            return filingClaimPage;
        }
        return null;
    }

    public List<FilingClaimPage> findAllPublishedSortedByName(User user) {
        List<FilingClaimPage> filingClaimPages = filingClaimPageRepository.findAllByStatusAndDeletedFalse(PageStatus.PUBLISHED, ServiceUtils.sortByFieldAscIgnoreCase("name"));
        Iterator<FilingClaimPage> iterator = filingClaimPages.iterator();

        while (iterator.hasNext()) {
            FilingClaimPage filingClaimPage = iterator.next();
            VendorPage vendorPage = filingClaimPage.getVendorPage();
            if (PageStatus.DISABLED.equals(vendorPage.getStatus())) {
                iterator.remove();
                continue;
            }
            if (vendorPage.getVendor().isTest()) {
                if(user == null || !user.getRoles().contains(Role.ROLE_ADMIN) && !vendorPage.getVendor().getLongTestUserIds().contains(user.getId())) {
                    iterator.remove();
                    continue;
                }
            }
            if (vendorPage.getVendor().isShowPureConsumers() && (user == null || user.getRoles().size() == 0 || user.getRoles().size() == 1 && user.getRoles().contains(Role.ROLE_USER))) {
                iterator.remove();
                continue;
            }
            Hibernate.initialize(filingClaimPage.getFilingClaimContacts());
        }

        return filingClaimPages;
    }

    public List<FilingClaimPage> findAllSortedByName() {
        List<FilingClaimPage> filingClaimPages = filingClaimPageRepository.findAllByDeletedFalse(ServiceUtils.sortByFieldAscIgnoreCase("name"));
  /*      for (FilingClaimPage filingClaimPage : filingClaimPages) {
            Hibernate.initialize(filingClaimPage.getFilingClaimContacts());
        } */
        return filingClaimPages;
    }

    public List<FilingClaimPage> getAllPublished() {
        return filingClaimPageRepository.findByStatusAndDeletedFalse(PageStatus.PUBLISHED);
    }

    public FilingClaimPage saveFilingClaimPage(FilingClaimPage page) {
        FilingClaimPage newPage;
        page.setModifiedDate(new Date());
        page.setPageType(pageTypeService.getDefaultPageType());
        page.setDescription(StringUtils.EMPTY);
        page.setContent(StringUtils.EMPTY);
        String filingClaimPageName = TextUtils.generateCmsName(page.getCaption());
        if (page.getId() != null && (newPage = filingClaimPageRepository.findOne(page.getId())) != null) {
            newPage.setCaption(page.getCaption());
            newPage.setDescription(page.getDescription());
            newPage.setContent(page.getContent().replaceAll("(\r\n|\n)", ""));
            newPage.setModifiedDate(page.getModifiedDate());
            newPage.setPageType(page.getPageType());
            newPage.setStatus(page.getStatus());
            newPage.setVendorPage(page.getVendorPage());
            newPage.setPhoneNumber(page.getPhoneNumber());
            newPage.setSchedulePerDay(page.getSchedulePerDay());
            newPage.setSchedulePerWeek(page.getSchedulePerWeek());
            newPage.setName(filingClaimPageName);
            newPage.setDescription(page.getDescription());
            newPage.setDeleted(page.isDeleted());
            return filingClaimPageRepository.save(newPage);
        }
        page.setName(filingClaimPageName);
        page.setCreateDate(new Date());
        page.setAuthor(SecurityHelper.getCurrentUser());
        return filingClaimPageRepository.save(page);
    }

    public String getPageTemplatePath(Page page) {
        return TEMPLATE_PATH + page.getPageType().getTemplate();
    }

    public void deleteFilingClaimPage(Long filingClaimPageId) {
        FilingClaimPage filingClaimPage = getFilingClaimPage(filingClaimPageId);
        if (filingClaimPage == null) {
            return;
        }
        List<FilingClaimContact> filingClaimContacts = filingClaimPage.getFilingClaimContacts();
        for (FilingClaimContact filingClaimContact : filingClaimContacts) {
            filingClaimContactService.deleteFilingClaimContact(filingClaimContact.getId());
        }
        filingClaimPage.setDeleted(true);
        filingClaimPage.setStatus(PageStatus.DISABLED);
        saveFilingClaimPage(filingClaimPage);
    }
}
