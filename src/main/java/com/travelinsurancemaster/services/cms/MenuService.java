package com.travelinsurancemaster.services.cms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelinsurancemaster.model.dto.cms.menu.AbstractMenu;
import com.travelinsurancemaster.model.dto.cms.menu.Menu;
import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.cms.menu.MenuWrapper;
import com.travelinsurancemaster.model.dto.cms.menu.validator.MenuItemValidator;
import com.travelinsurancemaster.model.dto.cms.page.Page;
import com.travelinsurancemaster.repository.cms.MenuItemRepository;
import com.travelinsurancemaster.repository.cms.MenuRepository;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.ServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Chernov Artur on 27.07.15.
 */

@Service
@Transactional
public class MenuService {
    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PageService pageService;

    public Menu get(Long id) {
        return menuRepository.findOne(id);
    }

    public Menu get(String title) {
        return menuRepository.findByTitle(title);
    }

    public List<Menu> getAllSortedByName() {
        return menuRepository.findAll(ServiceUtils.sortByFieldAscIgnoreCase("title"));
    }

    public List<MenuItem> getByPage(Page page) {
        return menuItemRepository.findByPage(page);
    }

    public List<MenuItem> getByMenuTitle(String menuTitle) {
        return menuItemRepository.findByMenuTitle(menuTitle);
    }

    public Menu save(Menu menu) {
        Menu newMenu;
        if (menu.getId() != null && (newMenu = menuRepository.findOne(menu.getId())) != null) {
            newMenu.setTitle(menu.getTitle());
            return menuRepository.save(newMenu);
        }
        return menuRepository.save(menu);
    }

    public MenuItem saveItem(MenuItem item) {
        return menuItemRepository.save(item);
    }


    public void delete(Long id) {
        menuRepository.delete(id);
    }

    public MenuWrapper getMenuWrapper(Long id) {
        Menu menu = get(id);
        if (menu == null) {
            return null;
        }
        return wrapMenu(menu);
    }

    public MenuWrapper validateMenu(String data, BindingResult errors) {
        List<MenuWrapper> menus;
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        try {
            menus = mapper.readValue(data, new TypeReference<List<MenuWrapper>>() {
            });
        } catch (Exception e) {
            log.debug("error making object from json string", e);
            errors.reject(null, "Create menu error");
            return null;
        }
        if (CollectionUtils.isEmpty(menus)) {
            log.debug("json menu is empty");
            errors.reject(null, "Create menu error");
            return null;
        }
        MenuWrapper rootMenu = menus.get(0);
        Menu menu = get(NumberUtils.parseLong(rootMenu.getId()));
        if (menu == null) {
            log.debug("error updating menu from json");
            errors.reject(null, "Update menu error");
            return null;
        }
        if (rootMenu.getChildren() != null) {
            for (MenuWrapper menuWrapper : rootMenu.getChildren()) {
                validateMenuItem(menuWrapper, errors, 1);
            }
        }
        return rootMenu;
    }

    private void validateMenuItem(MenuWrapper menuItem, BindingResult errors, int lvl) {
        MenuItemValidator menuItemValidator = new MenuItemValidator();
        menuItemValidator.validateMenuItem(menuItem, errors, lvl);
        if (menuItem.getChildren() != null) {
            List<MenuWrapper> childrens = menuItem.getChildren();
            for (MenuWrapper child : childrens) {
                validateMenuItem(child, errors, lvl + 1);
            }
        }
    }

    public void rebuildMenu(MenuWrapper rootMenu) {
        Menu menu = get(NumberUtils.parseLong(rootMenu.getId()));
        if (rootMenu.getChildren() != null) {
            List<MenuWrapper> childrens = rootMenu.getChildren();
            int i = 0;
            for (MenuWrapper child : childrens) {
                updateMenuItemsFromWrapper(child, 1, menu, i++);
            }
        }
        Set<Long> currentMenuChildIds = new HashSet<>();
        getMenuChildIds(menu.getMenuItems(), currentMenuChildIds);
        removeNewMenuIdsFromCurrentMenuIds(rootMenu.getChildren(), currentMenuChildIds);
        for (Long id : currentMenuChildIds) {
            MenuItem menuItem = menuItemRepository.findOne(id);
            if (menuItem != null && menuItem.getMenu() != null && menuItem.getMenu().getMenuItems() != null) {
                menuItem.getMenu().getMenuItems().remove(menuItem);
            }
            menuItemRepository.delete(menuItem);
        }
    }

    private void getMenuChildIds(List<MenuItem> children, Set<Long> childIds) {
        for (MenuItem menuItem : children) {
            childIds.add(menuItem.getId());
            if (menuItem.getChildMenuItems() != null) {
                getMenuChildIds(menuItem.getChildMenuItems(), childIds);
            }
        }
    }

    private void removeNewMenuIdsFromCurrentMenuIds(List<MenuWrapper> children, Set<Long> childIds) {
        for (MenuWrapper child : children) {
            childIds.remove(NumberUtils.parseLong(child.getId()));
            if (child.getChildren() != null) {
                removeNewMenuIdsFromCurrentMenuIds(child.getChildren(), childIds);
            }
        }
    }

    private void updateMenuItemsFromWrapper(MenuWrapper menuItem, int lvl, AbstractMenu parent, int sortOrder) {
        Optional<Long> id = Optional.ofNullable(NumberUtils.parseLong(menuItem.getId()));
        MenuItem item;
        if (id.isPresent()) {
            item = menuItemRepository.findOne(id.get());
        } else {
            item = new MenuItem();
        }
        item.setTitle(menuItem.getTitle() != null ? menuItem.getTitle().trim() : null);
        item.setLevel(lvl);

        Optional<Long> pageId = Optional.ofNullable(menuItem.getPageId());
        Page page = null;
        if (pageId.isPresent()) {
            page = pageService.getPage(pageId.get());
        }
        item.setPage(page);
        item.setUrl(menuItem.getUrl() != null ? menuItem.getUrl().trim() : null);
        if (parent instanceof Menu) {
            item.setMenu((Menu) parent);
        } else if (parent instanceof MenuItem) {
            item.setParentMenuItem((MenuItem) parent);
        }
        item.setSortOrder(sortOrder);
        item = menuItemRepository.save(item);
        if (menuItem.getChildren() != null) {
            int i = 0;
            for (MenuWrapper child : menuItem.getChildren()) {
                updateMenuItemsFromWrapper(child, lvl + 1, item, i++);
            }
        }
    }

    private MenuWrapper wrapMenu(Menu menu) {
        MenuWrapper menuWrapper = getMenuWrapper(menu);
        for (MenuItem menuItem : menu.getMenuItems()) {
            menuWrapper.getChildren().add(wrapMenuItem(menuItem));
        }
        return menuWrapper;
    }

    private MenuWrapper wrapMenuItem(MenuItem menuItemParam) {
        MenuWrapper menuWrapper = getMenuWrapper(menuItemParam);
        if (CollectionUtils.isNotEmpty(menuItemParam.getChildMenuItems())) {
            for (MenuItem menuItem : menuItemParam.getChildMenuItems()) {
                menuWrapper.getChildren().add(wrapMenuItem(menuItem));
            }
        }
        return menuWrapper;
    }

    public MenuWrapper getMenuWrapper(Menu menu) {
        MenuWrapper menuWrapper = new MenuWrapper();
        menuWrapper.setId(menu.getId().toString());
        menuWrapper.setTitle(menu.getTitle());
        menuWrapper.setLabel(menu.getTitle());
        return menuWrapper;
    }

    public MenuWrapper getMenuWrapper(MenuItem menuItem) {
        MenuWrapper menuWrapper = new MenuWrapper();
        menuWrapper.setId(menuItem.getId().toString());
        menuWrapper.setTitle(menuItem.getTitle());
        menuWrapper.setLabel(menuItem.getTitle());
        menuWrapper.setUrl(menuItem.getUrl());
        if (menuItem.getPage() != null) {
            menuWrapper.setPageId(menuItem.getPage().getId());
        }
        return menuWrapper;
    }
}
