package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PlaywrightLocalTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
            jsonObject.addProperty("browser_version", "latest");
            jsonObject.addProperty("os", "osx");
            jsonObject.addProperty("os_version", "catalina");
            jsonObject.addProperty("name", "Playwright first local test");
            jsonObject.addProperty("build", "playwright-build-3");
            jsonObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
            jsonObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
            jsonObject.addProperty("browserstack.local", "true");
            
            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(jsonObject.toString(), "utf-8");
            String ws_endpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;
            Browser browser = chromium.connect(ws_endpoint);
            Page page = browser.newPage();
            page.navigate("https://www.google.co.in/");
            Locator locator = page.locator("[aria-label='Search']");
            locator.click();
            page.fill("[aria-label='Search']", "BrowserStack");
            page.locator("[aria-label='Google Search'] >> nth=0").click();
            String title = page.title();

            if (title.equals("BrowserStack - Google Search")) {
                // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
                Object result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"passed\", \"reason\": \"Title matched\"}}");
                System.out.println(result);
            } else {
                Object result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"failed\", \"reason\": \"Title did not matched\"}}");
                System.out.println(result);
            }
            browser.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
    }
}
