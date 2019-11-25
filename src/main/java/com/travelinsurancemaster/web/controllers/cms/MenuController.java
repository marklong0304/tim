package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.model.dto.cms.menu.Menu;
import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.cms.menu.MenuWrapper;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.model.dto.json.JsonSimpleResult;
import com.travelinsurancemaster.services.cms.MenuService;
import com.travelinsurancemaster.services.cms.PageService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 27.07.15.
 */

@Controller
@Scope(value = "request")
@RequestMapping(value = "/cms/menu")
public class MenuController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuService menuService;

    @Autowired
    private PageService pageService;

    private List<Page> pages;

    @ModelAttribute("pages")
    public List<Page> getPages() {
        return pages;
    }

    @PostConstruct
    public void init() {
        pages = pageService.getAllPublished();
    }

    private void setNavigation(MenuWrapper menu, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/cms/menu", "Menu");
        if (menu != null && menu.getId() != null) {
            map.put("/cms/menu/build/" + String.valueOf(menu.getId()), menu.getTitle());
        }
        model.addAttribute("breadcrumb", map);
        model.addAttribute("backUrl", "/cms/menu");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String getMenus(Model model) {
        log.debug("Getting menus");
        model.addAttribute("menus", menuService.getAllSortedByName());
        return "cms/menu/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String createMenu(Model model) {
        log.debug("Getting create menu page");
        model.addAttribute("menu", new Menu());
        return "cms/menu/edit";
    }

    @RequestMapping(value = "/edit/{menuId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editMenu(@PathVariable("menuId") Long menuId, Model model) {
        log.debug("Getting edit menu page");
        Menu menu = menuService.get(menuId);
        model.addAttribute("menu", menu);
        return "cms/menu/edit";
    }

    @RequestMapping(value = "/build/{menuId}", method = RequestMethod.GET)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String buildMenu(@PathVariable("menuId") Long menuId, Model model) {
        log.debug("Getting build menu page");
        MenuWrapper menuWrapper = menuService.getMenuWrapper(menuId);
        setNavigation(menuWrapper, model);
        model.addAttribute("menu", menuWrapper);
        model.addAttribute("jsonMenu", JsonUtils.getJsonString(Collections.singleton(menuWrapper)));
        return "cms/menu/build";
    }

    @ResponseBody
    @RequestMapping(value = "/build/{menuId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public JsonSimpleResult buildMenu(@RequestParam("data") String data) {
        log.debug("Getting build menu page");
        BindingResult bindingResult = new BeanPropertyBindingResult(new MenuItem(), "menuItem");
        MenuWrapper menuWrapper = menuService.validateMenu(data, bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors().stream().collect(Collectors.toList());
            return new JsonSimpleResult("error", errors);
        }
        menuService.rebuildMenu(menuWrapper);
        return new JsonSimpleResult("success");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String editMenu(@Valid @ModelAttribute("menu") Menu menu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Processing menu update: menu={}, bindingResult = {}", menu, bindingResult);
        try {
            menu = menuService.save(menu);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (menu.getId() != null) {
            redirectAttributes.addFlashAttribute("lastModifiedMenuId", menu.getId());
        }
        return "redirect:/cms/menu";
    }

    @RequestMapping(value = "/delete/{menuId}", method = RequestMethod.POST)
    @Secured({"ROLE_CONTENT_MANAGER", "ROLE_ADMIN"})
    public String deleteMenu(@PathVariable("menuId") Long menuId) {
        log.debug("Delete menu by id={} action", menuId);
        menuService.delete(menuId);
        return "redirect:/cms/menu";
    }
}
