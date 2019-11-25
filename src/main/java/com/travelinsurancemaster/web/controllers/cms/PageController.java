package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.model.dto.cms.page.PageType;
import com.travelinsurancemaster.model.dto.cms.page.validator.PageValidator;
import com.travelinsurancemaster.services.cms.PageService;
import com.travelinsurancemaster.services.cms.PageTypeService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/page")
public class PageController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private PageService pageService;

    @Autowired
    private PageTypeService pageTypeService;

    @Autowired
    private PageValidator pageValidator;

    private List<PageType> pageTypes;

    @ModelAttribute("pageTypes")
    public List<PageType> getPageTypes() {
        return pageTypes;
    }

    @PostConstruct
    public void init() {
        pageTypes = pageTypeService.findNotSystem();
    }

    private void setNavigation(Page page, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/page", "Page");
        if (page != null && page.getId() != null) {
            map.put("/cms/page/edit/" + String.valueOf(page.getId()), page.getName());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/page");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getPages(Model model) {
        log.debug("Getting pages");
        model.addAttribute("pages", pageService.findAllNotSystemSortedByName());
        model.addAttribute("systemPages", pageService.findAllSystemPagesSortedByName());
        return "cms/page/list";
    }

    @RequestMapping(value = {"/create", "/edit"}, method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createPage(Model model) {
        log.debug("Getting create page");
        Page page = new Page();
        page.setPageType(pageTypeService.getDefaultPageType());
        model.addAttribute("page", page);
        setNavigation(page, model);
        return "cms/page/edit";
    }

    @RequestMapping(value = "/edit/{pageId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPage(@PathVariable("pageId") Long pageId, Model model) {
        log.debug("Getting edit page page");
        Page page = pageService.getPage(pageId);
        setNavigation(page, model);
        model.addAttribute("page", page);
        return "cms/page/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPage(@Valid @ModelAttribute("page") Page page, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Processing page update: page={}, bindingResult = {}", page, bindingResult);
        page.setName(page.getName() != null ? page.getName().trim() : null);
        page.setDescription(page.getDescription() != null ? page.getDescription().trim() : null);
        pageValidator.validate(page, bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(page, model);
            return "cms/page/edit";
        }
        try {
            page = pageService.savePage(page);
        } catch (ObjectOptimisticLockingFailureException e) {
            bindingResult.reject("optimistic.locking.error");
            log.warn("Optimistic locking error occurred when trying to create the page", e);
            return "cms/page/edit";
        }
        if (page.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedPageId", page.getId());
        }
        return "redirect:/cms/page";
    }

    @RequestMapping(value = "/delete/{pageId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deletePage(@PathVariable("pageId") Long pageId) {
        log.debug("Delete page by id={} action", pageId);
        pageService.deletePage(pageId);
        return "redirect:/cms/page";
    }

    @RequestMapping(value = "/wysiwyg")
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getWysiwyg(Model model) {
        return "cms/page/wysiwyg";
    }
}
