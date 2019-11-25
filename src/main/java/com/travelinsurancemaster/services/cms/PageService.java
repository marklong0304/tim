package com.travelinsurancemaster.services.cms;


import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.cms.page.BasePage;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.model.dto.cms.page.PageStatus;
import com.travelinsurancemaster.repository.cms.PageRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import com.travelinsurancemaster.util.TextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Chernov Artur on 23.07.15.
 */
@Service
@Transactional(readOnly = true)
public class PageService {
    private static final Logger log = LoggerFactory.getLogger(PageService.class);
    public static final String TEMPLATE_PATH = "/cms/page/template/";
    public static final String TEMPLATE_RAW_PATH = "/cms/page/template_raw/";

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageTypeService pageTypeService;

    @Autowired
    private MenuService menuService;

    private EntityManager entityManager;

    @Autowired
    public PageService(final EntityManagerFactory entityManagerFactory) { this.entityManager = entityManagerFactory.createEntityManager(); }

    @PostConstruct
    private void init() throws InterruptedException {
        Long count = pageRepository.countByStatusAndDeletedFalse(PageStatus.PUBLISHED);
        int indexSize = search(null, -1).size();
        if (indexSize != count.intValue()) {
            log.info("rebuild index for Page {}:{}", count, indexSize);
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer(Page.class).startAndWait();
        }
    }

    public Page getPage(Long id) {
        return pageRepository.findOne(id);
    }

    public Page getPage(String name) {
        List<Page> pages = pageRepository.findByNameAndDeletedFalse(name);
        if (CollectionUtils.isNotEmpty(pages)) {
            return pages.get(0);
        }
        return null;
    }

    public List<Page> findAllNotSystemSortedByName() {
        return pageRepository.findAllByPageTypeNotAndDeletedFalse(pageTypeService.getSystemPageType(), ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<Page> findAllSystemPagesSortedByName() {
        return pageRepository.findAllByPageTypeAndDeletedFalse(pageTypeService.getSystemPageType(), ServiceUtils.sortByFieldAscIgnoreCase("caption"));
    }

    public List<Page> getAllPublished() {
        return pageRepository.findByStatusAndDeletedFalse(PageStatus.PUBLISHED);
    }

    @Transactional(readOnly = false)
    public Page savePage(Page page) {
        Page newPage;
        page.setModifiedDate(new Date());
        String name = TextUtils.generateCmsName(page.getCaption());
        if (page.getId() != null && (newPage = pageRepository.findOne(page.getId())) != null) {
            newPage.setCaption(page.getCaption());
            newPage.setDescription(page.getDescription());
            newPage.setContent(page.getContent().replaceAll("(\r\n|\n)", ""));
            newPage.setModifiedDate(page.getModifiedDate());
            newPage.setPageType(page.getPageType());
            newPage.setStatus(page.getStatus());
            if (!Objects.equals(name, newPage.getName())) {
                List<MenuItem> items = menuService.getByPage(page);
                for (MenuItem item : items) {
                    item.setUrl("/page/" + name);
                    menuService.saveItem(item);
                }
            }
            newPage.setName(name);
            return pageRepository.save(newPage);
        }
        page.setName(name);
        page.setCreateDate(new Date());
        page.setAuthor(SecurityHelper.getCurrentUser());
        return pageRepository.save(page);
    }

    @Transactional(readOnly = false)
    public void deletePage(Long id) {
        Page page = getPage(id);
        page.setDeleted(true);
        page.setStatus(PageStatus.DISABLED);
        savePage(page);
    }

    public String getPageTemplatePath(Page page) {
        return TEMPLATE_PATH + page.getPageType().getTemplate();
    }

    public Page getProvidersPage() {
        return pageRepository.findByNameAndPageTypeAndDeletedFalse("providers", pageTypeService.getSystemPageType());
    }

    public Page getIndexPage() {
        return pageRepository.findByNameAndPageTypeAndDeletedFalse("index", pageTypeService.getSystemPageType());
    }

    public List<BasePage> search(String searchStr, int maxResults) {
        log.debug("search {}, limit {}", searchStr, maxResults);
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Page.class).get();

        Query query;
        if (StringUtils.isBlank(searchStr)) {
            query = queryBuilder.all().createQuery();
        } else {
            /*query = queryBuilder.phrase().withSlop(2)
                    .onField("name")
                    .andField("caption").boostedTo(3)
                    .andField("description")
                    .andField("content").boostedTo(2)
                    .sentence(searchStr.trim().toLowerCase()).createQuery();*/
            query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1)
                    .onField("name")
                    .andField("caption").boostedTo(3)
                    .andField("description")
                    .andField("content").boostedTo(2)
                    .matching(searchStr.trim().toLowerCase()).createQuery();
            /*query = queryBuilder.keyword().wildcard()
                    .onFields("name", "caption", "description", "content")
                    .matching(searchStr.trim().toLowerCase()).createQuery();*/
        }

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Page.class)
                .setProjection("id", "name", "caption", "description", "content")
                .setResultTransformer(new AliasToBeanResultTransformer(Page.class));

        if (maxResults > 0) {
            fullTextQuery.setMaxResults(maxResults);
        }

        @SuppressWarnings("unchecked")
        List<BasePage> pages = fullTextQuery.getResultList();

        log.debug("search result size {}", pages.size());
        return pages;
    }
}
