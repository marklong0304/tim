package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.services.QuoteStorageService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * @author Alexander.Isaenco
 */
@Controller
@RequestMapping(value = "planQuotes")
public class PlanQuotesController extends AbstractController{

    @Autowired
    private QuoteStorageService quoteStorageService;

    @RequestMapping( method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String current(Model model,
                          @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate dateFrom,
                          @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_FORMAT) LocalDate dateTo) {
        model.addAttribute("quotes", quoteStorageService.getSavedQuotesForCurrentUser(SecurityHelper.getCurrentUser(), dateFrom, dateTo));
        return "plan_quotes";
    }

    @RequestMapping(value = "delete/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String deleteQuote(@PathVariable("quoteId") String quoteId) {
        quoteStorageService.deleteQuoteStorage(quoteId);
        return "planQuotes";
    }

}