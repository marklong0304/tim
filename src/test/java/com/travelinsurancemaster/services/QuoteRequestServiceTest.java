package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.forms.Step1QuoteRequestForm;
import com.travelinsurancemaster.model.webservice.common.forms.Step2QuoteRequestForm;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class QuoteRequestServiceTest {
    private QuoteRequestService service = new QuoteRequestService();
    @Test
    public void testTripCostRounding() throws Exception {
        ExtendedModelMap model = new ExtendedModelMap();

        Step1QuoteRequestForm form1 = new Step1QuoteRequestForm();
        form1.setTripCost(new BigDecimal(1000));
        form1.setTripCostTotal(true);

        Step2QuoteRequestForm form2 = new Step2QuoteRequestForm();
        form2.setTravelers(asList(new GenericTraveler(25),
                                  new GenericTraveler(25),
                                  new GenericTraveler(25)));

        model.addAttribute("step1Form", form1);
        model.addAttribute("step2Form", form2);

        QuoteRequest quoteRequest = service.prepareQuoteRequestAfterStep2(model);

        assertEquals(1000, quoteRequest.getTripCost().intValue());
        assertEquals(334, quoteRequest.getTripCostPerTraveler().intValue()); // should round 1000/3=333.3 to 334
    }


}