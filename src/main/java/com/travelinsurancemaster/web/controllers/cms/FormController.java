package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.services.AffiliateService;
import com.travelinsurancemaster.web.controllers.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander.Isaenco
 */
@Controller
@RequestMapping("/form")
public class FormController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(FormController.class);

    @Autowired
    private AffiliateService affiliateService;

    @RequestMapping(value = "/{formName}", method = RequestMethod.GET)
    public String getForm(@PathVariable("formName") String formName) {
        return "redirect:/404";
    }

    @RequestMapping(value = "/{formName}", method = RequestMethod.POST)
    @ResponseBody
    public String postForm(@PathVariable("formName") String formName, @RequestParam MultiValueMap parameters) {
        if (StringUtils.isBlank(formName)) {
            return "error";
        }
        log.debug("post form {} with params: {}", formName, parameters);
        try {
            switch (formName) {
                case "affiliate":
                    return "success";
                default:
                    log.error("no " + formName + " for postForm");
                    return "error";
            }
        } catch (Exception e) {
            log.error("postForm error: " + e.getMessage(), e);
            return "error";
        }
    }


}
