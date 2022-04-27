package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import java.net.URLEncoder;
import java.util.HashMap;

import com.browserstack.local.Local;

public class PlaywrightLocalUsingBindingsTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            JsonObject capabilitiesObject = new JsonObject();
            capabilitiesObject.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
            capabilitiesObject.addProperty("browser_version", "latest");
            capabilitiesObject.addProperty("os", "osx");
            capabilitiesObject.addProperty("os_version", "catalina");
            capabilitiesObject.addProperty("name", "Playwright first local test");
            capabilitiesObject.addProperty("build", "playwright-java-3");
            capabilitiesObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
            capabilitiesObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
            capabilitiesObject.addProperty("browserstack.local", "true");

            //Creates an instance of Local
            Local bsLocal = new Local();

            // You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
            HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
            bsLocalArgs.put("key", "BROWSERSTACK_ACCESS_KEY");

            // Starts the Local instance with the required arguments
            bsLocal.start(bsLocalArgs);

            // Check if BrowserStack local instance is running
            System.out.println("BrowserStackLocal running: " + bsLocal.isRunning());

            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(capabilitiesObject.toString(), "utf-8");
            String ws_endpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;
            Browser browser = chromium.connect(ws_endpoint);
            Page page = browser.newPage();
            try {
                page.navigate("http://localhost:45691");
                page.waitForFunction("document" +
                        ".querySelector(\"body\")" +
                        ".innerText" +
                        ".includes(\"This is an internal server for BrowserStack Local\")");

                markTestStatus("passed", "Local is up and running", page);
            } catch (Exception err) {
                markTestStatus("failed", "BrowserStack Local binary is not running", page);
            }
            browser.close();

            //Stop the Local instance
            bsLocal.stop();
        } catch (Exception err) {
            System.out.println(err);
        }
    }

    public static void markTestStatus(String status, String reason, Page page) {
        Object result;
        result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
