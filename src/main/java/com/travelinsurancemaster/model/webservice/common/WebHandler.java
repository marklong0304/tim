package com.travelinsurancemaster.model.webservice.common;

/*
 * Created by raman on 13.08.19
 */

import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebHandler {

    public static final long TIMEOUT_IN_SECONDS = 60;

    private WebDriver webDriver;
    private WebDriverWait webDriverWait;
    private JavascriptExecutor je;

    protected InsuranceMasterApiProperties apiProperties;

    public WebHandler(String webDriverPath, String url) {
        System.setProperty("webdriver.chrome.driver", webDriverPath);
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless", "--no-sandbox");
        //webDriver = new ChromeDriver(options);
        webDriver = new ChromeDriver();
        je = (JavascriptExecutor) webDriver;
        webDriverWait = new WebDriverWait(webDriver, TIMEOUT_IN_SECONDS);
        webDriver.get(url);
    }

    public WebDriver getWebDriver() { return webDriver; }
    public void setWebDriver(WebDriver webDriver) { this.webDriver = webDriver; }

    public WebDriverWait getWebDriverWait() { return webDriverWait; }
    public void setWebDriverWait(WebDriverWait webDriverWait) { this.webDriverWait = webDriverWait; }

    public JavascriptExecutor getJe() { return je; }
    public void setJe(JavascriptExecutor je) { this.je = je; }
}