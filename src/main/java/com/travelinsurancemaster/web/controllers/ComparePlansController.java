package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.ProductSortService;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.Category;
import com.travelinsurancemaster.model.dto.Group;
import com.travelinsurancemaster.model.dto.json.JsonCompareResult;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.ComparePlansService;
import com.travelinsurancemaster.services.GroupService;
import com.travelinsurancemaster.services.InsuranceClientFacade;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by artur on 16.09.16.
 */
@Controller
@RequestMapping(value = "comparePlans")
@Scope(value = "request")
public class ComparePlansController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ComparePlansController.class);
    private static final String PARAMETERS_SEPARATOR = ";";
    private static final String CURRENT_PAGE = "ComparePlans";

    @Autowired
    private ComparePlansService comparePlansService;

    @Autowired
    private QuoteInfoSession quoteInfoSession;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private InsuranceClientFacade insuranceClientFacade;

    private List<Group> groups;

    @PostConstruct
    public void init() {
        groups = groupService.getAllWithFilterCategoriesSortedByFilterOrder();
    }

    @ModelAttribute("groups")
    public List<Group> getGroups() {
        return groups;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showComparePlansPage(@RequestParam Map<String, String> requestParameters, Model model) {
        String quoteId = requestParameters.get("quoteId");
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        if (quoteRequest == null) {
            return "redirect:/";
        }
        /*
        String bestPlanCode = requestParameters.get("bestPlanCode");
        if (bestPlanCode == null) {
            return "redirect/404";
        }
        */
        model.addAttribute("sortOrder", quoteInfo.getSortOrder());
        model.addAttribute("quoteId", quoteId);
        //model.addAttribute("bestPlanCode", bestPlanCode);
        model.addAttribute("quoteRequest", quoteRequest);
        model.addAttribute("plans", requestParameters.get("plans"));
        model.addAttribute("quoteRequestJson", JsonUtils.getJsonString(quoteRequest));
        return "compare/comparePlans";
    }

    @RequestMapping(value = "baseResults/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true, beforeInvocation = true)
    public String baseResults(@PathVariable("quoteId") String quoteId, @RequestParam Map<String, String> params) {

        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        if (quoteRequest == null) {
            return "redirect:/";
        }
        String bestPlanCode = params.get("bestPlanCode");
        if (bestPlanCode == null) {
            return "redirect/404";
        }

        List<String> planIds = StringUtils.isNotBlank(params.get("plans")) ? Arrays.asList(params.get("plans").split(PARAMETERS_SEPARATOR)) : null;

        return JsonUtils.getJsonString(this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest, planIds));
    }

    @RequestMapping(value = "changePlanType/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changePlanType(@PathVariable("quoteId") String quoteId, @RequestParam("requestId") String requestId,
                                         @RequestParam("planType") String planType, @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return ResponseEntity.notFound().build();
        }
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.setPlanType(PlanType.valueOf(planType));
        List<String> planIds = Arrays.asList(params.get("plans").split(PARAMETERS_SEPARATOR));
        List<Category> cancellationCategories = groupService.getGroupByName(GroupNames.Cancellation).getCategoryList().stream()
                .filter(category -> quoteRequest.getCategories().containsKey(category.getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cancellationCategories)) {
            cancellationCategories.forEach(category -> quoteRequest.getCategories().remove(category.getCode()));
        }
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY);
        } else {
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_CANCELLATION);
        }
        return ResponseEntity.ok("Ok");
    }

    @RequestMapping(value = "preparedResults/{quoteId}/{requestId}", method = RequestMethod.GET)
    @ResponseBody
    public String getPreparedResults(@PathVariable(value = "quoteId") String quoteId,
                                     @PathVariable("requestId") String requestId,
                                     @RequestParam(value = "includedPolicies", required = false) Set<String> includedPolicies,
                                     @RequestParam(value = "bestPlanCode") String bestPlanCode,
                                     HttpServletRequest request) {

        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, false);
        QuoteRequest quoteRequest;
        if (quoteInfo == null) {
            quoteRequest = quoteInfoSession.getQuoteRequest(quoteId);
        } else {
            quoteRequest = quoteInfo.getQuoteRequest();
        }
        if (quoteRequest == null) {
            return null;
        }

        CompareResult compareResult = this.quoteInfoSession.getCompareResult(quoteInfo, quoteRequest, UUID.fromString(requestId), includedPolicies);
        return JsonUtils.getJsonString(comparePlansService.getJsonComparePlansResult(compareResult, quoteRequest, bestPlanCode, request));
    }

    @RequestMapping(value = "stopRequest/{requestId}", method = RequestMethod.POST)
    public ResponseEntity<String> stopRequest(@PathVariable("requestId") String requestId) {
        if (requestId.equals("null")) {
            return new ResponseEntity<>("OK!", HttpStatus.NO_CONTENT);
        }

        this.insuranceClientFacade.stopRequest(UUID.fromString(requestId));
        return new ResponseEntity<>("OK!", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "updateCategories/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    public String updateCategories(
            @PathVariable("quoteId") String quoteId,
            @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        List<String> planIds = Arrays.asList(params.get("plans").split(PARAMETERS_SEPARATOR));
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.getCategories().clear();
        boolean hasCancellationGroupParam = false;
        for (Group group : groups) {
            for (Category category : group.getCategoryList()) {
                if (params.containsKey(category.getCode())) {
                    if (category.getType() == Category.CategoryType.CATALOG
                            && NumberUtils.toInt(params.get(category.getCode()), -1) == -1) {
                        quoteRequest.getCategories().remove(category.getCode());
                    } else {
                        quoteRequest.getCategories().put(category.getCode(), params.get(category.getCode()));
                        if (GroupService.isCancellationGroup(category, quoteRequest.getPlanType())) {
                            hasCancellationGroupParam = true;
                        }
                    }
                } else {
                    quoteRequest.getCategories().remove(category.getCode());
                }
            }
        }
        if (hasCancellationGroupParam && !params.containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        }
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_INTERRUPTION_RETURN_AIR_ONLY);
        } else {
            quoteRequest.getCategories().remove(CategoryCodes.TRIP_CANCELLATION);
        }
        quoteInfo.setOriginal(false);

        JsonCompareResult result = this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest, new ArrayList<>(planIds));

        return JsonUtils.getJsonString(result);
    }

    @RequestMapping(value = "updateSortOrder/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    public String changeSortOrder(
            @PathVariable("quoteId") String quoteId,
            @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        List<String> planIds = Arrays.asList(params.get("plans").split(PARAMETERS_SEPARATOR));
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        String sortOrderParam = StringUtils.upperCase(params.get("sortOrder"));
        SortOrder sortOrder = EnumUtils.getEnum(SortOrder.class, sortOrderParam);
        if (sortOrder == null) {
            return "redirect:/";
        }
        quoteInfo.setSortOrder(sortOrder);

        JsonCompareResult result = this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest, new ArrayList<>(planIds));

        return JsonUtils.getJsonString(result);
    }

    @RequestMapping(value = "reset/{quoteId}", method = RequestMethod.POST)
    @ResponseBody
    public String reset(
            @PathVariable("quoteId") String quoteId,
            @RequestParam Map<String, String> params) {
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId);
        if (quoteInfo == null) {
            return "redirect:/";
        }
        List<String> planIds = Arrays.asList(params.get("plans").split(PARAMETERS_SEPARATOR));
        QuoteRequest quoteRequest = quoteInfo.getQuoteRequest();
        quoteRequest.getCategories().clear();
        quoteInfo.setOriginal(false);
        if (quoteRequest.getPlanType().getId() == PlanType.COMPREHENSIVE.getId()) {
            quoteRequest.getCategories().put(CategoryCodes.TRIP_CANCELLATION, CategoryCodes.TRIP_CANCELLATION);
        }

        JsonCompareResult result = this.quoteInfoSession.startFilterProductsRequest(quoteInfo, quoteRequest, new ArrayList<>(planIds));

        return JsonUtils.getJsonString(result);
    }

    @Override
    protected String getCurrentPageName(Model model) {
        return CURRENT_PAGE;
    }

    @RequestMapping(value = "/{quoteId}", method = RequestMethod.GET)
    public String getSearchResults(@PathVariable("quoteId") String quoteId, Model model, @RequestParam Map<String, String> params) {
        RestoreType restoreType = params.containsKey("restore") ? RestoreType.STORAGE : RestoreType.NAN;
        QuoteInfo quoteInfo = quoteInfoSession.getQuoteInfo(quoteId, false, restoreType);
        QuoteRequest quoteRequest;
        if (quoteInfo == null) {
            quoteRequest = quoteInfoSession.getQuoteRequest(quoteId);
        } else {
            quoteRequest = quoteInfo.getQuoteRequest();
        }
        if (quoteRequest == null) {
            return "redirect:/";
        }

        model.addAttribute("quoteId", quoteId);
        model.addAttribute("plans", "");
        model.addAttribute("sortOrder", quoteInfo != null ? quoteInfo.getSortOrder() : ProductSortService.DEFAULT_ORDER);
        model.addAttribute("quoteRequest", quoteRequest);
        model.addAttribute("quoteRequestJson", JsonUtils.getJsonString(quoteRequest));
        return "compare/comparePlans";
    }
}
