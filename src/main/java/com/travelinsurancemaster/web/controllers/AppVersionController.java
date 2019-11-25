package com.travelinsurancemaster.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Raman on 23.09.19.
 */

@Controller
@RequestMapping(value = "version")
public class AppVersionController {

    private static final Logger log = LoggerFactory.getLogger(AppVersionController.class);

    @Value( "${version}" )
    private String version;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppVersion> getAppVersion(Model model) {
        log.debug("Getting app version, version = " + version);
        AppVersion appVersion = new AppVersion(version);
        return ResponseEntity.ok(appVersion);
    }

    private class AppVersion {
        String version;
        public AppVersion(String version) {
            this.version = version;
        }
        public String getVersion() { return version; }
    }
}