package com.travelinsurancemaster.web.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander.Isaenco
 */
@Controller
public class CustomErrorController extends AbstractController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);

    @Value("${error.path:/error}")
    private String errorPath;

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        //return this.errorPath;
        //HACK to exclude error path from IgnoredPathsWebSecurityConfigurerAdapter
        return "/fake/error/path";
    }

    @RequestMapping(value = "${error.path:/error}", produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request) {
        return new ModelAndView("error", getErrorAttributes(request, false));
    }

    @RequestMapping(value = "${error.path:/error}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus((Integer) body.get("status"));
        return new ResponseEntity<>(body, status);
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttribute;
        AccessDeniedException accessDeniedException = (AccessDeniedException) request.getAttribute(WebAttributes.ACCESS_DENIED_403);
        if (accessDeniedException != null) {
            errorAttribute = new HashMap<>();
            errorAttribute.put("timestamp", new Date());
            errorAttribute.put("path", null);
            errorAttribute.put("status", HttpStatus.FORBIDDEN.value());
            errorAttribute.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
            errorAttribute.put("message", accessDeniedException.getMessage());
            errorAttribute.put("exception", accessDeniedException.getClass().getName());
            if (includeStackTrace) {
                errorAttribute.put("trace", ExceptionUtils.getStackTrace(accessDeniedException));
            }
        } else {
            RequestAttributes requestAttributes = new ServletRequestAttributes(request);
            errorAttribute = errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
            //fix 999 for /error
            if (Integer.valueOf(999).equals(errorAttribute.get("status")) && request.getRequestURI().contains("/error")) {
                errorAttribute.put("status", 404);
                errorAttribute.put("error", "Not found");
                errorAttribute.put("message", "Not found");
            }
        }
        log.error("/error path: {}, status: {}, error: {}", errorAttribute.get("path"), errorAttribute.get("status"), errorAttribute.get("error"));
        return errorAttribute;
    }

    private HttpStatus getStatus(Integer statusCode) {
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
