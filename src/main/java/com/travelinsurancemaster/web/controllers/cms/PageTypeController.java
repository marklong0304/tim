package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.cms.page.PageType;
import com.travelinsurancemaster.services.cms.PageTypeService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Created by Chernov Artur on 23.07.15.
 */

@Controller
@RequestMapping(value = "/cms/pagetype")
public class PageTypeController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(PageTypeController.class);

    @Autowired
    private PageTypeService pageTypeService;

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getPageTypes(Model model) {
        log.debug("Getting page types");
        model.addAttribute("types", pageTypeService.findAllSortedByName());
        return "cms/page/type/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createPageType(Model model) {
        log.debug("Getting create page type page");
        model.addAttribute("pageType", new PageType());
        return "cms/page/type/edit";
    }

    @RequestMapping(value = "/edit/{pageTypeId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPageType(@PathVariable("pageTypeId") Long pageTypeId, Model model) {
        log.debug("Getting edit page type page");
        PageType pageType = pageTypeService.get(pageTypeId);
        model.addAttribute("pageType", pageType);
        return "cms/page/type/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editPageType(@Valid @ModelAttribute("pageType") PageType pageType, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Processing page type update: pageType={}, bindingResult = {}", pageType, bindingResult);
        try {
            pageType = pageTypeService.save(pageType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (pageType.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedPageTypeId", pageType.getId());
        }
        return "redirect:/cms/pagetype";
    }

    @RequestMapping(value = "/delete/{pageTypeId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deletePageType(@PathVariable("pageTypeId") Long pageTypeId) {
        log.debug("Delete page type by id={} action", pageTypeId);
        pageTypeService.delete(pageTypeId);
        return "redirect:/cms/pagetype";
    }
}
