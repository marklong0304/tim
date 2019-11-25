package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.json.UpsaleResponseDTO;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.UpsaleService;
import com.travelinsurancemaster.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Artur Chernov
 */


@Controller
@RequestMapping(value = "/upsale")
public class UpsaleController {

    @Autowired
    private UpsaleService upsaleService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @ResponseBody
    @RequestMapping(value = "/package/enable", method = RequestMethod.POST)
    public String emablePackage(@RequestParam String packageCode,
                              @RequestParam String policyUniqueCode,
                              @RequestParam String quoteRequestJson,
                                @RequestParam Boolean enabled) {
        QuoteRequest quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        if (quoteRequest == null) {
            throw new RuntimeException("no quote request");
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyUniqueCode);
        if (policyMeta == null) {
            throw new RuntimeException("no policy meta");
        }
        return JsonUtils.getJsonString(upsaleService.changePackageStatus(policyMeta, quoteRequest, packageCode, enabled));
    }

    @ResponseBody
    @RequestMapping(value = "/tooltips", method = RequestMethod.POST)
    public String getBasicTooltips(@RequestParam String policyUniqueCode,
                                @RequestParam String quoteRequestJson) {
        QuoteRequest quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        if (quoteRequest == null) {
            throw new RuntimeException("no quote request");
        }
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaByUniqueCode(policyUniqueCode);
        if (policyMeta == null) {
            throw new RuntimeException("no policy meta");
        }
        UpsaleResponseDTO upsaleResponseDTO = new UpsaleResponseDTO();
        upsaleService.fillTooltips(quoteRequest, policyMeta, upsaleResponseDTO);
        return JsonUtils.getJsonString(upsaleResponseDTO);
    }
}
