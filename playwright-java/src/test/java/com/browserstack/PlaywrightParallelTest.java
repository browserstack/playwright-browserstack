package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;

import java.net.URLEncoder;
import java.util.ArrayList;

class RunSession implements Runnable {
    private JsonObject caps;

    RunSession(JsonObject capabilities) {
        this.caps = capabilities;
    }

    public void run() {
        try (Playwright playwright = Playwright.create()) {
            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(this.caps.toString(), "utf-8");
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
        System.out.println(result);
    }
}

public class PlaywrightParallelTest {
    public static void main(String[] args) throws Exception {
        ArrayList<JsonObject> capabilitiesList = getCapabilitiesList();

        ArrayList<Thread> threadList = new ArrayList<Thread>();

        for (JsonObject capabilities: capabilitiesList) {
            capabilities.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
            capabilities.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
            Thread sessionThread = new Thread(new RunSession(capabilities));
            threadList.add(sessionThread);
            sessionThread.start();
        }

        for (Thread thread: threadList) {
            thread.join();
        }
    }

    public static ArrayList<JsonObject> getCapabilitiesList() {
        ArrayList<JsonObject> capabilitiesList = new ArrayList<JsonObject>();

        JsonObject catalinaChromeCap = new JsonObject();
        catalinaChromeCap.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaChromeCap.addProperty("browser_version", "latest");
        catalinaChromeCap.addProperty("os", "osx");
        catalinaChromeCap.addProperty("os_version", "catalina");
        catalinaChromeCap.addProperty("name", "Branded Google Chrome on Catalina");
        catalinaChromeCap.addProperty("build", "playwright-java-2");
        capabilitiesList.add(catalinaChromeCap);
        
        JsonObject catalinaEdgeCap = new JsonObject();
        catalinaEdgeCap.addProperty("browser", "edge");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaEdgeCap.addProperty("browser_version", "latest");
        catalinaEdgeCap.addProperty("os", "osx");
        catalinaEdgeCap.addProperty("os_version", "catalina");
        catalinaEdgeCap.addProperty("name", "Branded Microsoft Edge on Catalina");
        catalinaEdgeCap.addProperty("build", "playwright-java-2");
        capabilitiesList.add(catalinaEdgeCap);

        JsonObject catalinaFirefoxCap = new JsonObject();
        catalinaFirefoxCap.addProperty("browser", "playwright-firefox");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaFirefoxCap.addProperty("os", "osx");
        catalinaFirefoxCap.addProperty("os_version", "catalina");
        catalinaFirefoxCap.addProperty("name", "Playwright firefox on Catalina");
        catalinaFirefoxCap.addProperty("build", "playwright-java-2");
        capabilitiesList.add(catalinaFirefoxCap);

        JsonObject catalinaWebkitCap = new JsonObject();
        catalinaWebkitCap.addProperty("browser", "playwright-webkit"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaWebkitCap.addProperty("os", "osx");
        catalinaWebkitCap.addProperty("os_version", "catalina");
        catalinaWebkitCap.addProperty("name", "Playwright webkit on Catalina");
        catalinaWebkitCap.addProperty("build", "playwright-java-2");
        capabilitiesList.add(catalinaWebkitCap);

        JsonObject catalinaChromiumCap = new JsonObject();
        catalinaChromiumCap.addProperty("browser", "playwright-chromium"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaChromiumCap.addProperty("os", "osx");
        catalinaChromiumCap.addProperty("os_version", "catalina");
        catalinaChromiumCap.addProperty("name", "Playwright webkit on Catalina");
        catalinaChromiumCap.addProperty("build", "playwright-java-2");
        capabilitiesList.add(catalinaChromiumCap);

        return capabilitiesList;
    }
}
