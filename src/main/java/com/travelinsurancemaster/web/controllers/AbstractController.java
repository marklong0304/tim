package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.cms.menu.MenuItem;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.services.cms.MenuService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.util.ApplicationVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Created by Chernov Artur on 29.07.15.
 */
@Controller
public class AbstractController {
    private static final String CURRENT_PAGE = "Default";
    private static final String HEADER_MENU = "header_menu";
    private static final String FOOTER_MENU = "footer_menu";
    private static final String SUCCESS_MESSAGE = "successMessage";

    @Value("${com.travelinsurancemaster.debug}")
    private boolean isDebug;

    @Value("${session.statusCheckInterval}")
    private Integer statusCheckInterval;

    @Value("${google.analytics.enable}")
    private boolean gaEnable;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private ApplicationVersion appVersion;

    @ModelAttribute("headerMenuItems")
    public List<MenuItem> getHeaderMenu() {
        return menuService.getByMenuTitle(HEADER_MENU);
    }

    @ModelAttribute("footerMenuItems")
    public List<MenuItem> getFooterMenu() {
        return menuService.getByMenuTitle(FOOTER_MENU);
    }

    @ModelAttribute("statusCheckInterval")
    public Integer getStatusCheckInterval() {
        return statusCheckInterval * 1000;
    }

    @ModelAttribute("gaEnable")
    public boolean isGaEnable() {
        return gaEnable;
    }

    @ModelAttribute("isDebug")
    public boolean isDebug() {
        return isDebug;
    }

    @ModelAttribute("defaultSettings")
    public SystemSettings getSystemSettings() {
        return systemSettingsService.getDefault();
    }

    @ModelAttribute("appVersion")
    public ApplicationVersion getAppVersion() {
        return appVersion;
    }

    protected void addMessageSuccess(String message, Model model) {
        model.addAttribute(SUCCESS_MESSAGE, message);
    }

    @ModelAttribute("currentPage")
    protected String getCurrentPageName(Model model){
        return CURRENT_PAGE;
    }
}
