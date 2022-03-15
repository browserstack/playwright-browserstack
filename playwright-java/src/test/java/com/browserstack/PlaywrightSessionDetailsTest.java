package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.google.gson.JsonParser;

import java.net.URLEncoder;

public class PlaywrightSessionDetailsTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            JsonObject capabilitiesObject = new JsonObject();
            capabilitiesObject.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
            capabilitiesObject.addProperty("browser_version", "latest");
            capabilitiesObject.addProperty("os", "osx");
            capabilitiesObject.addProperty("os_version", "catalina");
            capabilitiesObject.addProperty("name", "Playwright first single test");
            capabilitiesObject.addProperty("build", "playwright-java-5");
            capabilitiesObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
            capabilitiesObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");

            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(capabilitiesObject.toString(), "utf-8");
            String ws_endpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;
            Browser browser = chromium.connect(ws_endpoint);
            Page page = browser.newPage();
            try {
                page.navigate("https://www.google.co.in/");
                Locator locator = page.locator("[aria-label='Search']");
                locator.click();
                page.fill("[aria-label='Search']", "BrowserStack");
                page.locator("[aria-label='Google Search'] >> nth=0").click();
                String title = page.title();

                if (title.equals("BrowserStack - Google Search")) {
                    // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
                    markTestStatus("passed", "Title matched", page);
                } else {
                    markTestStatus("failed", "Title did not match", page);
                }

                // store the JSON response in the Object class
                Object response = page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"getSessionDetails\"}");
                System.out.println(response);

                // parse the JSON response
                JsonObject json = JsonParser.parseString((String) response).getAsJsonObject();

                // store session ID in a variable
                String sessionID = String.valueOf(json.get("hashed_id"));

                // print session ID in your IDE's console
                System.out.println(sessionID);
            } catch (Exception err) {
                markTestStatus("failed", err.getMessage(), page);
            }
            browser.close();
        } catch (Exception err) {
            System.out.println(err);
        }
    }
    public static void markTestStatus(String status, String reason, Page page) {
        Object result;
        result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
