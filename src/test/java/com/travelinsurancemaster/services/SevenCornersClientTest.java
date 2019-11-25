package com.travelinsurancemaster.services;

;
;

/**
 * Created by ritchie on 2/13/15.
 */
/*
@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class SevenCornersClientTest extends AbstractRestClient {

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    @Autowired
    private SevenCornersClient sevenCornersClient;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Autowired
    private TestCreditCards creditCards;

    @Autowired
    private Mapper mapper;

    private JWT jwt;

    public SevenCornersClientTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Test
    public void quote() {
        QuoteResult quoteResult = new QuoteResult();

        SCQuoteRequest scQuoteRequest = mapper.createQuoteRequest(quoteRequest, policyMeta, policyMetaCode);
        HttpEntity<SCQuoteRequest> requestEntity = new HttpEntity<>(scQuoteRequest, createAuthHeaders());
        ResponseEntity<SCQuoteResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getUrl() + "/quote", HttpMethod.POST, requestEntity, SCQuoteResponse.class);
        SCQuoteResponse quoteResponse = response.getBody();

        SCPurchaseResponse purchaseResponse = response.getBody();
        purchaseResponse.setStatus(Result.Status.ERROR);

        assertTrue(purchaseResponse.getPolicyCertificates().size() > 0);
        assertEqual(purchaseResponse.getPolicyCertificates().get(0).getCertificateNumber()(purchaseResponse.getPolicyCertificates().get(0).getCertificateNumber(), "1");
    }

    @Test
    public void purchase() {

        RestTemplate restTemplate = new RestTemplate();
        PurchaseRequest purchaseRequest = new PurchaseRequest();

        SCPurchaseRequest scPurchaseRequest = mapper.createPurchaseRequest(purchaseRequest, apiProperties.getSevenCorners());
        HttpEntity<SCPurchaseRequest> requestEntity = new HttpEntity<>(scPurchaseRequest, createAuthHeaders());
        ResponseEntity<SCPurchaseResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getUrl() + "/purchase", HttpMethod.POST, requestEntity, SCPurchaseResponse.class);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        SCPurchaseResponse scPurchaseResponse = response.getBody();
        purchaseResponse.setStatus(Result.Status.SUCCESS);
        assert(scPurchaseResponse.getPolicyCertificates() != null && scPurchaseResponse.getPolicyCertificates().size() > 0);
        purchaseResponse.setPolicyNumber(scPurchaseResponse.getPolicyCertificates().get(0).getCertificateNumber());
    }

    private HttpHeaders createAuthHeaders(){
        checkJwt();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Authorization", "Bearer " + jwt.getJwt());
        return requestHeaders;
    }

    private void checkJwt() {
        if (jwt == null || jwt.isExpired()) {
            requestNewJWT();
        }
    }
    private void requestNewJWT() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", apiProperties.getSevenCorners().getAuthClientId());
        map.add("grant_type", apiProperties.getSevenCorners().getAuthGrantType());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(apiProperties.getSevenCorners().getAuthUrl(), HttpMethod.POST, request, AuthResponse.class);
        jwt = new JWT(response.getBody().getAccessToken(), response.getBody().getExpiresIn());
    }

    @Override
    public boolean auth() {
        checkJwt();
        return true;
    }
}
*/

