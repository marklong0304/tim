package com.travelinsurancemaster.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Alexander.Isaenco
 */
@Component
@ConfigurationProperties(prefix = "com.travelinsurancemaster.api")
public class InsuranceMasterApiProperties {

    private int timeout;
    private int corePoolSize;
    private int maxPoolSize;

    private String webDriverPath;

    private Allianz allianz;
    private CSA csa;
    private iTravelInsured iTravelInsured;
    private SevenCorners sevenCorners;
    private TravelInsured travelInsured;
    private TravelSafe travelSafe;
    private Trawick trawick;
    private TravelGuard travelGuard;
    private HCCMISAtlasTravel hCCMISAtlasTravel;
    private HCCMISAtlasMultiTrip hCCMISAtlasMultiTrip;
    private HCCMISStudentSecure hCCMISStudentSecure;
    private TravelexInsurance travelexInsurance;
    private HTHTravelInsurance hTHTravelInsurance;
    private HTHTravelInsuranceSingleTrip hTHTravelInsuranceSingleTrip;
    private HTHTravelInsuranceTripProtector hTHTravelInsuranceTripProtector;
    private HTHTravelInsuranceTravelGap hTHTravelInsuranceTravelGap;
    private MHRoss mHRoss;
    private UsaAssist usaAssist;
    private RoamRight roamRight;
    private BHTravelProtection bHTravelProtection;
    private TravelInsure travelInsure;
    private GlobalAlert globalAlert;

    public GlobalAlert getGlobalAlert() {
        return globalAlert;
    }

    public void setGlobalAlert(GlobalAlert globalAlert) {
        this.globalAlert = globalAlert;
    }

    public TravelInsure getTravelInsure() {
        return travelInsure;
    }

    public void setTravelInsure(TravelInsure travelInsure) {
        this.travelInsure = travelInsure;
    }

    public BHTravelProtection getbHTravelProtection() {
        return bHTravelProtection;
    }

    public void setbHTravelProtection(BHTravelProtection bHTravelProtection) {
        this.bHTravelProtection = bHTravelProtection;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getWebDriverPath() { return webDriverPath; }

    public void setWebDriverPath(String webDriverPath) { this.webDriverPath = webDriverPath; }

    public Allianz getAllianz() {
        return allianz;
    }

    public void setAllianz(Allianz allianz) {
        this.allianz = allianz;
    }

    public CSA getCsa() {
        return csa;
    }

    public void setCsa(CSA csa) {
        this.csa = csa;
    }

    public InsuranceMasterApiProperties.iTravelInsured getiTravelInsured() {
        return iTravelInsured;
    }

    public void setiTravelInsured(InsuranceMasterApiProperties.iTravelInsured iTravelInsured) {
        this.iTravelInsured = iTravelInsured;
    }

    public SevenCorners getSevenCorners() {
        return sevenCorners;
    }

    public void setSevenCorners(SevenCorners sevenCorners) {
        this.sevenCorners = sevenCorners;
    }

    public TravelInsured getTravelInsured() {
        return travelInsured;
    }

    public void setTravelInsured(TravelInsured travelInsured) {
        this.travelInsured = travelInsured;
    }

    public TravelSafe getTravelSafe() {
        return travelSafe;
    }

    public void setTravelSafe(TravelSafe travelSafe) {
        this.travelSafe = travelSafe;
    }

    public Trawick getTrawick() {
        return trawick;
    }

    public void setTrawick(Trawick trawick) {
        this.trawick = trawick;
    }

    public TravelGuard getTravelGuard() {
        return travelGuard;
    }

    public void setTravelGuard(TravelGuard travelGuard) {
        this.travelGuard = travelGuard;
    }

    public HCCMISAtlasTravel gethCCMISAtlasTravel() {
        return hCCMISAtlasTravel;
    }

    public void sethCCMISAtlasTravel(HCCMISAtlasTravel hCCMISAtlasTravel) {
        this.hCCMISAtlasTravel = hCCMISAtlasTravel;
    }

    public HCCMISAtlasMultiTrip gethCCMISAtlasMultiTrip() {
        return hCCMISAtlasMultiTrip;
    }

    public void sethCCMISAtlasMultiTrip(HCCMISAtlasMultiTrip hCCMISAtlasMultiTrip) {
        this.hCCMISAtlasMultiTrip = hCCMISAtlasMultiTrip;
    }

    public HCCMISStudentSecure gethCCMISStudentSecure() {
        return hCCMISStudentSecure;
    }

    public void sethCCMISStudentSecure(HCCMISStudentSecure hCCMISStudentSecure) {
        this.hCCMISStudentSecure = hCCMISStudentSecure;
    }

    public TravelexInsurance getTravelexInsurance() {
        return travelexInsurance;
    }

    public void setTravelexInsurance(TravelexInsurance travelexInsurance) {
        this.travelexInsurance = travelexInsurance;
    }

    public HTHTravelInsurance gethTHTravelInsurance() {
        return hTHTravelInsurance;
    }

    public void sethTHTravelInsurance(HTHTravelInsurance hTHTravelInsurance) {
        this.hTHTravelInsurance = hTHTravelInsurance;
    }

    public HTHTravelInsuranceSingleTrip gethTHTravelInsuranceSingleTrip() {
        return hTHTravelInsuranceSingleTrip;
    }

    public void sethTHTravelInsuranceSingleTrip(HTHTravelInsuranceSingleTrip hTHTravelInsuranceSingleTrip) {
        this.hTHTravelInsuranceSingleTrip = hTHTravelInsuranceSingleTrip;
    }

    public HTHTravelInsuranceTripProtector gethTHTravelInsuranceTripProtector() {
        return hTHTravelInsuranceTripProtector;
    }

    public void sethTHTravelInsuranceTripProtector(HTHTravelInsuranceTripProtector hTHTravelInsuranceTripProtector) {
        this.hTHTravelInsuranceTripProtector = hTHTravelInsuranceTripProtector;
    }

    public HTHTravelInsuranceTravelGap gethTHTravelInsuranceTravelGap() {
        return hTHTravelInsuranceTravelGap;
    }

    public void sethTHTravelInsuranceTravelGap(HTHTravelInsuranceTravelGap hTHTravelInsuranceTravelGap) {
        this.hTHTravelInsuranceTravelGap = hTHTravelInsuranceTravelGap;
    }

    public MHRoss getmHRoss() {
        return mHRoss;
    }

    public void setmHRoss(MHRoss mHRoss) {
        this.mHRoss = mHRoss;
    }

    public UsaAssist getUsaAssist() {
        return usaAssist;
    }

    public void setUsaAssist(UsaAssist usaAssist) {
        this.usaAssist = usaAssist;
    }

    public RoamRight getRoamRight() {
        return roamRight;
    }

    public void setRoamRight(RoamRight roamRight) {
        this.roamRight = roamRight;
    }

    public abstract static class Credentials {
        private String user;
        private String password;
        private String url;

        public Credentials() {
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Allianz extends Credentials {
        private String quoteUrl;
        private String purchaseUrl;
        private String accam;
        private String agent;

        public String getQuoteUrl() {
            return quoteUrl;
        }

        public void setQuoteUrl(String quoteUrl) {
            this.quoteUrl = quoteUrl;
        }

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        public String getAccam() {
            return accam;
        }

        public void setAccam(String accam) {
            this.accam = accam;
        }

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }
    }

    public static class CSA extends Credentials {
        private String agentId;
        private String agentEmail;

        public String getAgentEmail() {
            return agentEmail;
        }

        public void setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        @Override
        public String toString() {
            return "CSA" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "agentId='" + agentId + '\'' + "\n" +
                    "agentEmail='" + agentEmail + '\'';
        }
    }

    public static class iTravelInsured extends Credentials {
        private BigDecimal referralFeePercentage;
        private BigDecimal referralFeeDollarAmt;
        private String agentEmail;

        public iTravelInsured() {
            agentEmail = null;
        }

        public BigDecimal getReferralFeeDollarAmt() {
            return referralFeeDollarAmt;
        }

        public void setReferralFeeDollarAmt(BigDecimal referralFeeDollarAmt) {
            this.referralFeeDollarAmt = referralFeeDollarAmt;
        }

        public BigDecimal getReferralFeePercentage() {
            return referralFeePercentage;
        }

        public void setReferralFeePercentage(BigDecimal referralFeePercentage) {
            this.referralFeePercentage = referralFeePercentage;
        }

        public String getAgentEmail() {
            return agentEmail;
        }

        public void setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
        }

        @Override
        public String toString() {
            return "iTravelInsured" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "referralFeePercentage=" + referralFeePercentage + "\n" +
                    "referralFeeDollarAmt=" + referralFeeDollarAmt + "\n" +
                    "agentEmail='" + agentEmail + '\'' + "\n" + "\n";
        }
    }

    public static class SevenCorners extends Credentials {

        private String authUrl;
        private String authClientId;
        private String authGrantType;
        private String agentNumber;

        public String getAuthUrl() {
            return authUrl;
        }

        public void setAuthUrl(String authUrl) {
            this.authUrl = authUrl;
        }

        public String getAuthClientId() {
            return authClientId;
        }

        public void setAuthClientId(String authClientId) {
            this.authClientId = authClientId;
        }

        public String getAuthGrantType() {
            return authGrantType;
        }

        public void setAuthGrantType(String authGrantType) {
            this.authGrantType = authGrantType;
        }

        public String getAgentNumber() {
            return agentNumber;
        }

        public void setAgentNumber(String agentNumber) {
            this.agentNumber = agentNumber;
        }

        @Override
        public String toString() {
            return "SevenCorners" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "authUrl='" + authUrl + '\'' + "\n" +
                    "authClientId='" + authClientId + '\'' + "\n" +
                    "authGrantType='" + authGrantType + '\'' + "\n" + "\n" +
                    "agentNumber='" + agentNumber + '\'' + "\n" + "\n";
        }
    }

    public static class TravelInsured extends Credentials {
        private String agentSine;
        private String companyNameCode;

        public String getCompanyNameCode() {
            return companyNameCode;
        }

        public void setCompanyNameCode(String companyNameCode) {
            this.companyNameCode = companyNameCode;
        }

        public String getAgentSine() {
            return agentSine;
        }

        public void setAgentSine(String agentSine) {
            this.agentSine = agentSine;
        }

        @Override
        public String toString() {
            return "TravelInsured" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "agentSine='" + agentSine + '\'' + "\n" +
                    "companyNameCode='" + companyNameCode + '\'' + "\n" + "\n";
        }
    }

    public static class TravelSafe extends Credentials {
        private String quoteUrl;
        private String purchaseUrl;
        private String loc;

        public String getQuoteUrl() {
            return quoteUrl;
        }

        public void setQuoteUrl(String quoteUrl) {
            this.quoteUrl = quoteUrl;
        }

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        public String getLoc() {
            return loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        @Override
        public String toString() {
            return "TravelSafe" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "quoteUrl='" + quoteUrl + '\'' + "\n" +
                    "purchaseUrl='" + purchaseUrl + '\'' + "\n" +
                    "loc='" + loc + '\'' + "\n" + "\n";
        }
    }

    public static class Trawick extends Credentials {
        @Override
        public String toString() {
            return "Trawick" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class TravelGuard extends Credentials {
        private int arc;
        private String agentEmail;

        public int getArc() {
            return arc;
        }

        public void setArc(int arc) {
            this.arc = arc;
        }

        public String getAgentEmail() {
            return agentEmail;
        }

        public void setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
        }

        @Override
        public String toString() {
            return "TravelGuard" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "arc=" + arc + "\n" +
                    "agentEmail='" + agentEmail + '\'' + "\n" + "\n";
        }
    }

    public static class HCCMISAtlasTravel extends Credentials {
        @Override
        public String toString() {
            return "HCCMISAtlasTravel" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class HCCMISAtlasMultiTrip extends Credentials {
        @Override
        public String toString() {
            return "HCCMISAtlasMultiTrip" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class HCCMISStudentSecure extends Credentials {
        @Override
        public String toString() {
            return "HCCMISStudentSecure" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class TravelexInsurance extends Credentials {
        private String locationNumber;
        private String purchaseUrl;
        private String tsysUrl;

        public String getLocationNumber() {
            return locationNumber;
        }

        public void setLocationNumber(String locationNumber) {
            this.locationNumber = locationNumber;
        }

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        public String getTsysUrl() {
            return tsysUrl;
        }

        public void setTsysUrl(String tsysUrl) {
            this.tsysUrl = tsysUrl;
        }

        @Override
        public String toString() {
            return "TravelexInsurance" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "locationNumber='" + locationNumber + '\'' + "\n" +
                    "purchaseUrl='" + purchaseUrl + '\'' + "\n" +
                    "tsysUrl='" + tsysUrl + '\'' + "\n" + "\n";
        }
    }

    public static class HTHTravelInsurance extends Credentials {
        private String uniqueId;
        private String agentEmail;

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public String getAgentEmail() {
            return agentEmail;
        }

        public void setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
        }

        @Override
        public String toString() {
            return "HTHTravelInsurance" + "\n" +
                    "uniqueId='" + uniqueId + '\'' + "\n" +
                    "agentEmail='" + agentEmail + '\'' + "\n" + "\n";
        }
    }

    public static class HTHTravelInsuranceSingleTrip extends Credentials {
        @Override
        public String toString() {
            return "HTHTravelInsuranceSingleTrip" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class HTHTravelInsuranceTripProtector extends Credentials {
        @Override
        public String toString() {
            return "HTHTravelInsuranceTripProtector" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class HTHTravelInsuranceTravelGap extends Credentials {
        @Override
        public String toString() {
            return "HTHTravelInsuranceTravelGap" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n";
        }
    }

    public static class MHRoss extends Credentials {
        private String purchaseUrl;

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }
    }

    public static class UsaAssist extends Credentials {
        private String purchaseUrl;

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        @Override
        public String toString() {
            return "UsaAssist{" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "purchaseUrl='" + purchaseUrl + '\'' + "\n" + "\n";
        }
    }

    public static class RoamRight extends Credentials {

        private String webDriverPath;

        public String getWebDriverPath() { return webDriverPath; }
        public void setWebDriverPath(String webDriverPath) { this.webDriverPath = webDriverPath; }

        @Override
        public String toString() {
            return "RoamRight" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" + "\n" +
                    "webDriverPath='" + getWebDriverPath() + '\'' + "\n" + "\n";
        }
    }

    public static class BHTravelProtection extends  Credentials{

        private String agencyCode;
        private String agentCode;
        private String agentEmail;
        private String purchaseUrl;
        private String loginUrl;

        public String getLoginUrl() {
            return loginUrl;
        }

        public void setLoginUrl(String loginUrl) {
            this.loginUrl = loginUrl;
        }

        public String getAgencyCode() {
            return agencyCode;
        }

        public void setAgencyCode(String agencyCode) {
            this.agencyCode = agencyCode;
        }

        public String getAgentCode() {
            return agentCode;
        }

        public void setAgentCode(String agentCode) {
            this.agentCode = agentCode;
        }

        public String getAgentEmail() {
            return agentEmail;
        }

        public void setAgentEmail(String agentEmail) {
            this.agentEmail = agentEmail;
        }

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        @Override
        public String toString() {
            return "BHTravelProtection" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +  "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "agencyCode='" + agencyCode + '\'' + "\n" +
                    "agentCode='" + agentCode + '\'' + "\n" +
                    "agentEmail='" + agentEmail + '\'' + "\n" +
                    "purchaseUrl='" + purchaseUrl + '\'' + "\n" +
                    "loginUrl='" + loginUrl + '\'' + "\n" + "\n";
        }
    }

    public static class TravelInsure extends Credentials {
        private String authUrl;
        private String authClientId;
        private String authClientSecret;
        private String authScope;
        private String authGrantType;

        public String getAuthUrl() {
            return authUrl;
        }

        public void setAuthUrl(String authUrl) {
            this.authUrl = authUrl;
        }

        public String getAuthClientId() {
            return authClientId;
        }

        public void setAuthClientId(String authClientId) {
            this.authClientId = authClientId;
        }

        public String getAuthClientSecret() {
            return authClientSecret;
        }

        public void setAuthClientSecret(String authClientSecret) {
            this.authClientSecret = authClientSecret;
        }

        public String getAuthScope() {
            return authScope;
        }

        public void setAuthScope(String authScope) {
            this.authScope = authScope;
        }

        public String getAuthGrantType() {
            return authGrantType;
        }

        public void setAuthGrantType(String authGrantType) {
            this.authGrantType = authGrantType;
        }

        @Override
        public String toString() {
            return "TravelInsure" + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "authUrl='" + authUrl + '\'' + "\n" +
                    "authClientId='" + authClientId + '\'' + "\n" +
                    "authClientSecret='" + authClientSecret + '\'' + "\n" +
                    "authScope='" + authScope + '\'' + "\n" +
                    "authGrantType='" + authGrantType + '\'' + "\n" + "\n";
        }
    }

    public static class GlobalAlert extends Credentials {

        private String location;
        private String purchaseUrl;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPurchaseUrl() {
            return purchaseUrl;
        }

        public void setPurchaseUrl(String purchaseUrl) {
            this.purchaseUrl = purchaseUrl;
        }

        @Override
        public String toString() {
            return "GlobalAlert" + "\n" +
                    "user='" + getUser() + '\'' + "\n" +
                    "password='" + getPassword() + '\'' + "\n" +
                    "url='" + getUrl() + '\'' + "\n" +
                    "location='" + location + '\'' + "\n" +
                    "purchaseUrl='" + purchaseUrl + '\'' + "\n" + "\n";
        }
    }
}
