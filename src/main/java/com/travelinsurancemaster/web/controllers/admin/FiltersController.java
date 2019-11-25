package com.travelinsurancemaster.web.controllers.admin;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.json.datatable.filter.AffiliateJson;
import com.travelinsurancemaster.model.dto.json.datatable.filter.CompanyJson;
import com.travelinsurancemaster.model.dto.json.datatable.filter.UserJson;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/filters")
@Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT"})
public class FiltersController {

    @Autowired private UserService userService;

    @Autowired private CompanyService companyService;

    @Autowired private PurchaseService purchaseService;

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserJson> getUsers(@RequestParam("filterString") String filterString) {
    List<User> users = userService.getByEmailOrName(filterString).stream().filter(user -> {
        List<Purchase> purchases = purchaseService.getAllByUserId(user.getId()).stream()
                .filter(purchase -> purchase.isSuccess()).collect(Collectors.toList());;
        return !purchases.isEmpty();
    }).collect(Collectors.toList());
    return users.stream().map(UserJson::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/affiliates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AffiliateJson> getAffiliates(@RequestParam("filterString") String filterString) {
    List<User> users = userService.getAffiliateByEmailOrName(filterString).stream()
            .filter(user -> user.isAffiliate()).collect(Collectors.toList());
    return users.stream().map(AffiliateJson::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/companies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CompanyJson> getCompanies(@RequestParam("filterString") String filterString) {
        List<Company> companies = companyService.getCompanyByNameLike(filterString).stream().collect(Collectors.toList());
        return companies.stream().map(CompanyJson::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/travelers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getTravellers(@RequestParam("filterString") String filterString) {
    List<String> result = new ArrayList<>();
    purchaseService
        .getTravellersOfSuccessPurchase(filterString)
        .forEach(
            purchase -> {
              String travelerName =
                  purchase.getPurchaseQuoteRequest().getPrimaryTraveler().getFirstName()
                      + "|"
                      + purchase.getPurchaseQuoteRequest().getPrimaryTraveler().getLastName();
              if (!result.contains(travelerName)) {
                result.add(travelerName);
              }
            });
    return result;
    }
}
