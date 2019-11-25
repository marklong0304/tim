package com.travelinsurancemaster;

import com.travelinsurancemaster.clients.travelguard.GetQuote;
import com.travelinsurancemaster.clients.travelguard.GetQuoteResponse;
import com.travelinsurancemaster.clients.travelguard.Purchase;
import com.travelinsurancemaster.clients.travelguard.PurchaseResponse;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.webservice.travelguard.PolicyPurchaseDetailsTG;
import com.travelinsurancemaster.model.webservice.travelguard.PolicyQuoteDetailsTG;
import com.travelinsurancemaster.model.webservice.travelguard.PolicySpecificationTG;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.LoggingRequestInterceptor;
import com.travelinsurancemaster.services.clients.*;
import com.travelinsurancemaster.util.RestTemplateErrorHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

import javax.net.ssl.*;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.List;


@EnableWs
@Configuration
@EnableAsync
public class WsConfig extends WsConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(WsConfig.class);

    public static final String TLS_V_1_2 = "TLSv1.2";

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Bean(name = ApiVendor.TravelInsured)
    public TravelInsuredClient TravelInsuredClient() throws JAXBException {
        TravelInsuredClient travelInsuredClient = new TravelInsuredClient();
        travelInsuredClient.setDefaultUri(apiProperties.getTravelInsured().getUrl());
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.travelinsurancemaster.clients.travelinsured");
        travelInsuredClient.setMarshaller(marshaller);
        travelInsuredClient.setUnmarshaller(marshaller);
        return travelInsuredClient;
    }

    @Bean(name = ApiVendor.RoamRight)
    public RoamRightClient roamRightClient() {
        return new RoamRightClient();
    }

    @Bean(name = ApiVendor.ITravelInsured)
    public ITravelInsuredClient iTravelInsuredClient() {
        ITravelInsuredClient client = new ITravelInsuredClient();
        client.setDefaultUri(apiProperties.getiTravelInsured().getUrl());
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.travelinsurancemaster.clients.itravelinsured");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean(name = ApiVendor.TravelSafe)
    public TravelSafeClient travelsafeClient() {
        return new TravelSafeClient(formDataRestTemplate());
    }

    @Bean(name = ApiVendor.MHRoss)
    public MHRossClient mhRossClient() {
        return new MHRossClient(formDataRestTemplate());
    }

    @Bean(name = ApiVendor.CSA)
    public CSAClient csaClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.travelinsurancemaster.clients.csa.xsd");
        return new CSAClient(formDataRestTemplate(), marshaller);
    }

    @Bean(name = ApiVendor.TravelexInsurance)
    public TravelexInsuranceClient travelexInsuranceClient() {
        return new TravelexInsuranceClient(restTemplate());
    }

    private RestTemplate restTemplate() {
        if (LoggingRequestInterceptor.log.isTraceEnabled()) {
            RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(getFactory()));
            restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
            return restTemplate;
        } else {
            return new RestTemplate(getFactory());
        }
    }

    private ClientHttpRequestFactory getFactory(){
        try {
            SSLContext context = SSLContext.getInstance(TLS_V_1_2);
            context.init(null, null, null);
            CloseableHttpClient httpClient = HttpClientBuilder
                    .create()
                    .setSSLContext(context)
                    .build();
            return new HttpComponentsClientHttpRequestFactory(httpClient);
        } catch (Exception e) {
            log.error("could not create SSL Context - cause::: {}", e.getMessage(), e);
            e.printStackTrace();
        }
        return new SimpleClientHttpRequestFactory();
    }

    private RestTemplate getRestTemplateAnyHostNameOKTrustingAllCerts() {
        return getRestTemplateWithHttpRequestFactory(createClientHttpRequestFactoryAnyHostNameOKTrustingAllCerts());
    }

    private RestTemplate getRestTemplateWithHttpRequestFactory(ClientHttpRequestFactory httpRequestFactory) {
        if (LoggingRequestInterceptor.log.isTraceEnabled()) {
            RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(httpRequestFactory));
            restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
            return restTemplate;
        } else {
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            return restTemplate;
        }
    }

    private RestTemplate restTemplateGzipSupported() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        return getRestTemplateWithHttpRequestFactory(clientHttpRequestFactory);
    }

    private RestTemplate formDataRestTemplate() {
        RestTemplate restTemplate = restTemplate();
        HttpMessageConverter formHttpConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpConverter = new StringHttpMessageConverter();
        List<HttpMessageConverter<?>> converters =
                Arrays.asList(new HttpMessageConverter<?>[]{formHttpConverter, stringHttpConverter});
        restTemplate.setMessageConverters(converters);
        return restTemplate;
    }

    private RestTemplate restTemplateWithErrorHandle() {
        //RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));

        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        if (LoggingRequestInterceptor.log.isTraceEnabled()) {
            restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        }

        return restTemplate;
    }

    @Bean(name = ApiVendor.Allianz)
    public AllianzClient allianzClient() {
        WebServiceGatewaySupport purchaseClient = new WebServiceGatewaySupport() {
        };
        AllianzClient client = new AllianzClient(purchaseClient);
        client.setDefaultUri(apiProperties.getAllianz().getQuoteUrl());
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        //marshaller.setMarshallerProperties(Collections.singletonMap("com.sun.xml.internal.bind.namespacePrefixMapper", new AllianzClient.AllianzPrefixMapper())); - fix for Allianz
        marshaller.setContextPath("com.travelinsurancemaster.clients.allianz");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);

        purchaseClient.setMarshaller(marshaller);
        purchaseClient.setUnmarshaller(marshaller);
        purchaseClient.setDefaultUri(apiProperties.getAllianz().getPurchaseUrl());

        return client;
    }

    @Bean(name = ApiVendor.Trawick)
    public TrawickClient trawickClient() {
        return new TrawickClient(restTemplate());
    }

    @Bean(name = ApiVendor.UsaAssist)
    public UsaAssistClient usaAssistClient() {
        return new UsaAssistClient(restTemplate());
    }

    @Bean(name = ApiVendor.HTHTravelInsurance)
    public HTHTravelInsuranceClient hthTravelInsuranceClient() {
        return new HTHTravelInsuranceClient(restTemplateGzipSupported());
    }

    @Bean(name = ApiVendor.SevenCorners)
    public SevenCornersClient sevenCornersClient() {
        return new SevenCornersClient(restTemplate());
    }

    @Bean(name = ApiVendor.TravelGuard)
    public TravelGuardClient travelGuardClient() {
        TravelGuardClient travelGuardClient = new TravelGuardClient();
        travelGuardClient.setDefaultUri(apiProperties.getTravelGuard().getUrl());
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(PolicySpecificationTG.class, PolicyQuoteDetailsTG.class, GetQuote.class,
                GetQuoteResponse.class, Purchase.class, PurchaseResponse.class, PolicyPurchaseDetailsTG.class); // todo: change to setContextPath method
        travelGuardClient.setMarshaller(marshaller);
        travelGuardClient.setUnmarshaller(marshaller);
        return travelGuardClient;
    }

    @Bean(name = ApiVendor.HCCMedicalInsuranceServices)
    public HCCMedicalInsuranceServicesClient hccMedicalInsuranceServicesClient() {
        return new HCCMedicalInsuranceServicesClient(getRestTemplateAnyHostNameOKTrustingAllCerts());
    }

    @Bean(name = ApiVendor.BHTravelProtection)
    public BHTravelProtectionClient bhTravelProtectionClient() {
        return new BHTravelProtectionClient(restTemplateWithErrorHandle());
    }


    @Bean(name = ApiVendor.TravelInsure)
    public TravelInsureClient travelInsureClient() {
        return new TravelInsureClient(restTemplateWithErrorHandle());
    }


    @Bean(name = ApiVendor.GlobalAlert)
    public GlobalAlertClient globalAlertClient() {
        return new GlobalAlertClient(getRestTemplateAnyHostNameOKTrustingAllCerts());
    }


    //Get client trusting all servers and all certificates
    private ClientHttpRequestFactory createClientHttpRequestFactoryAnyHostNameOKTrustingAllCerts() {
        HostnameVerifier anyHostnameOKVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(anyHostnameOKVerifier).setSSLContext(createContextTrustingAllCerts()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return requestFactory;
    }

    //Get context trusting all certificates
    private SSLContext createContextTrustingAllCerts() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            SSLContext.setDefault(sc);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return sc;

        } catch (Exception e) {}
        return null;
    }
}