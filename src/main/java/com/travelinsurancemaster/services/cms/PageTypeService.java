package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.cms.page.PageType;
import com.travelinsurancemaster.repository.cms.PageTypeRepository;
import com.travelinsurancemaster.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Service
@Transactional
public class PageTypeService {
    private static final Logger log = LoggerFactory.getLogger(PageTypeService.class);

    public static final String SYSTEM_PAGE_TYPE = "System";
    public static final String DEFAULT_PAGE_TYPE = "Default";

    @Autowired
    private PageTypeRepository pageTypeRepository;

    public PageType get(Long id) {
        return pageTypeRepository.findOne(id);
    }

    public List<PageType> findNotSystem() {
        return findAllSortedByName().stream()
                .filter(pageType -> !PageTypeService.SYSTEM_PAGE_TYPE.equals(pageType.getName())).collect(Collectors.toList());
    }

    public List<PageType> findAllSortedByName() {
        return pageTypeRepository.findAll(ServiceUtils.sortByFieldAscIgnoreCase("name"));
    }

    public PageType save(PageType pageType) {
        PageType newPageType;
        if (pageType.getId() != null && (newPageType = pageTypeRepository.findOne(pageType.getId())) != null) {
            newPageType.setName(pageType.getName());
            newPageType.setTemplate(pageType.getTemplate());
            return pageTypeRepository.save(newPageType);
        }
        return pageTypeRepository.save(pageType);
    }

    public PageType getSystemPageType() {
        return pageTypeRepository.findByName(SYSTEM_PAGE_TYPE);
    }

    public PageType getDefaultPageType() {
        return pageTypeRepository.findByName(DEFAULT_PAGE_TYPE);
    }

    public void delete(Long id) {
        pageTypeRepository.delete(id);
    }
}
