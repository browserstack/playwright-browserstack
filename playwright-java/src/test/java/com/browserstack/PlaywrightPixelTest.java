package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PlaywrightPixelTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            JsonObject capabilitiesObject = new JsonObject();
            capabilitiesObject.addProperty("browser", "playwright-webkit");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
            capabilitiesObject.addProperty("browser_version", "latest");
            capabilitiesObject.addProperty("name", "Test on Playwright emulated Devices");
            capabilitiesObject.addProperty("build", "playwright-java-4");
            capabilitiesObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
            capabilitiesObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");

            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(capabilitiesObject.toString(), "utf-8");
            String ws_endpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;

            Browser browser = chromium.connect(ws_endpoint);
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4943.0 Mobile Safari/537.36")
                    .setViewportSize(393, 727)
                    .setScreenSize(393, 851)
                    .setDeviceScaleFactor(3)
                    .setIsMobile(true)
                    .setHasTouch(true));

            Page page = context.newPage();
            try {
                page.navigate("https://www.google.co.in/");
                Locator locator = page.locator("[aria-label='Search']");
                locator.click();
                page.fill("[aria-label='Search']", "BrowserStack");
                page.keyboard().press("Enter");
                page.locator("[aria-current='page']").waitFor();
                String title = page.title();

                if (title.equals("BrowserStack - Google Search")) {
                    // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
                    markTestStatus("passed", "Title matched", page);
                } else {
                    markTestStatus("passed", "Title did not match", page);
                }
            } catch (Exception err) {
                markTestStatus("failed", err.getMessage(), page);
            }
            browser.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
    }
    public static void markTestStatus(String status, String reason, Page page) {
      Object result;
      result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
