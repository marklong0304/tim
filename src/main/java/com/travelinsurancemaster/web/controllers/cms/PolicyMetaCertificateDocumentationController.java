package com.travelinsurancemaster.web.controllers.cms;

import com.travelinsurancemaster.web.controllers.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Chernov Artur on 21.12.2015.
 */

@Controller
@RequestMapping("/api/certificate")
public class PolicyMetaCertificateDocumentationController extends AbstractController {

    @RequestMapping(method = RequestMethod.GET)
    public String getApiDocumentation(HttpServletRequest request, Model model) {
        String path = request.getContextPath();
        model.addAttribute("path", path);
        return "/cms/page/documentation/api";
    }
}
