package com.travelinsurancemaster.services.cms;


import com.travelinsurancemaster.model.dto.cms.page.CategoryContent;
import com.travelinsurancemaster.repository.cms.CategoryContentRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 10.20.15.
 */
@Service
@Transactional(readOnly = true)
public class CategoryContentService {
    private static final Logger log = LoggerFactory.getLogger(CategoryContentService.class);

    @Autowired
    private CategoryContentRepository categoryContentRepository;

    public CategoryContent getCategoryContent(Long id) {
        return categoryContentRepository.findOne(id);
    }

    public CategoryContent getByCode(String code) {
        List<CategoryContent> categoryContentList = categoryContentRepository.findByCodeAndDeletedFalse(code);
        if (CollectionUtils.isNotEmpty(categoryContentList)) {
            return categoryContentList.get(0);
        }
        return null;
    }

    @Transactional(readOnly = false)
    public List<CategoryContent> getCategoryContentListForceRepairSort() {
        return forcedRepairSortOrders(getCategoryContentList());
    }


    public List<CategoryContent> getCategoryContentListWithContent() {
        List<CategoryContent> categoryContentList = getCategoryContentList();
        categoryContentList.forEach(categoryContent -> categoryContent.getContent());
        return categoryContentList;
    }

    public List<CategoryContent> getCategoryContentList() {
        return categoryContentRepository.findAllByDeletedFalse(ServiceUtils.sortByFieldAscIgnoreCase("sortOrder"));
    }

    public CategoryContent getBySortOrder(Integer sortOrder) {
        List<CategoryContent> categoryContentList = categoryContentRepository.findBySortOrderAndDeletedFalse(sortOrder);
        if (CollectionUtils.isNotEmpty(categoryContentList)) {
            return categoryContentList.get(0);
        }
        return null;
    }

    @Transactional(readOnly = false)
    public CategoryContent saveCategoryContent(CategoryContent categoryContent) {
        CategoryContent newCategoryContent;
        categoryContent.setModifiedDate(new Date());
        if (categoryContent.getId() != null && (newCategoryContent = categoryContentRepository.findOne(categoryContent.getId())) != null) {
            newCategoryContent.setContent(categoryContent.getContent().replaceAll("(\r\n|\n)", ""));
            newCategoryContent.setModifiedDate(categoryContent.getModifiedDate());
            newCategoryContent.setCategory(categoryContent.getCategory());
            newCategoryContent.setCode(categoryContent.getCode());
            newCategoryContent.setName(categoryContent.getName());
            newCategoryContent.setSortOrder(categoryContent.getSortOrder());
            return categoryContentRepository.save(newCategoryContent);
        }
        categoryContent.setCreateDate(new Date());
        categoryContent.setAuthor(SecurityHelper.getCurrentUser());
        categoryContent.setSortOrder(getCategoryContentList().size());
        return categoryContentRepository.save(categoryContent);
    }

    @Transactional(readOnly = false)
    public void deleteCategoryContent(Long id) {
        CategoryContent categoryContent = getCategoryContent(id);
        categoryContent.setDeleted(true);
        Integer sortIndex = categoryContent.getSortOrder();
        saveCategoryContent(categoryContent);
        updateSortOrders(sortIndex);
    }

    @Transactional(readOnly = false)
    public void swap(CategoryContent first, CategoryContent second) {
        int index = first.getSortOrder();
        first.setSortOrder(second.getSortOrder());
        second.setSortOrder(index);
        categoryContentRepository.save(first);
        categoryContentRepository.save(second);
    }

    private void updateSortOrders(Integer sortOrder) {
        List<CategoryContent> categoryContentList = getCategoryContentList();
        for (CategoryContent categoryContent : categoryContentList) {
            if (categoryContent.getSortOrder() > sortOrder) {
                categoryContent.setSortOrder(categoryContent.getSortOrder() - 1);
            }
        }
        categoryContentRepository.save(categoryContentList);
    }

    private List<CategoryContent> forcedRepairSortOrders(List<CategoryContent> categoryContentList) {
        boolean isCorrectOrder = true;
        int i = 0;
        for (CategoryContent categoryContent : categoryContentList) {
            if (categoryContent.getSortOrder() != i) {
                isCorrectOrder = false;
                categoryContent.setSortOrder(i);
            }
            i++;
        }
        if (!isCorrectOrder) {
            categoryContentList = categoryContentRepository.save(categoryContentList);
        }
        return categoryContentList;
    }

    @Transactional(readOnly = false)
    public List<CategoryContent> sort() {
        List<CategoryContent> categoryContentList = getCategoryContentList();
        Collections.sort(categoryContentList, (o1, o2) -> {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            return (res != 0) ? res : o1.getName().compareTo(o2.getName());
        });
        for (int i = 0; i < categoryContentList.size(); i++) {
            categoryContentList.get(i).setSortOrder(i);
        }
        categoryContentRepository.save(categoryContentList);
        return categoryContentList;
    }
}
