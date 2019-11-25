package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.model.dto.json.JsonResponse;
import com.travelinsurancemaster.model.dto.json.NoteJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;;

@Controller
@RequestMapping("/api/admin/commissions")
public class NoteController {

    @Autowired
    private PurchaseService purchaseService;

    @RequestMapping(value = "/note", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_AFFILIATE"})
    public JsonResponse editNote(@RequestBody String payload) {
        NoteJson request = JsonUtils.getObject(payload, NoteJson.class);
        Purchase purchase = purchaseService.getPurchase(request.getId());
        purchase.setNote(request.getNote());
        purchaseService.save(purchase);
        return new JsonResponse(true);
    }
}
