package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;

import java.net.URLEncoder;
import java.util.ArrayList;

class CombinationOne implements Runnable {
    public void run() {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        capabilitiesObject.addProperty("browser_version", "latest");
        capabilitiesObject.addProperty("os", "osx");
        capabilitiesObject.addProperty("os_version", "catalina");
        capabilitiesObject.addProperty("name", "Branded Google Chrome on Catalina");
        capabilitiesObject.addProperty("build", "playwright-build-2");

        PlaywrightParallelTest deviceOne = new PlaywrightParallelTest();
        deviceOne.executeTest(capabilitiesObject);
    }
}

class CombinationTwo implements Runnable {
    public void run() {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "edge");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        capabilitiesObject.addProperty("browser_version", "latest");
        capabilitiesObject.addProperty("os", "osx");
        capabilitiesObject.addProperty("os_version", "catalina");
        capabilitiesObject.addProperty("name", "Branded Microsoft Edge on Catalina");
        capabilitiesObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceTwo = new PlaywrightParallelTest();
        deviceTwo.executeTest(capabilitiesObject);
    }
}

class CombinationThree implements Runnable {
    public void run() {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "playwright-firefox");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        capabilitiesObject.addProperty("os", "osx");
        capabilitiesObject.addProperty("os_version", "catalina");
        capabilitiesObject.addProperty("name", "Playwright firefox on Catalina");
        capabilitiesObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceThree = new PlaywrightParallelTest();
        deviceThree.executeTest(capabilitiesObject);
    }
}

class CombinationFour implements Runnable {
    public void run() {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "playwright-webkit"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        capabilitiesObject.addProperty("os", "osx");
        capabilitiesObject.addProperty("os_version", "catalina");
        capabilitiesObject.addProperty("name", "Playwright webkit on Catalina");
        capabilitiesObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceFour = new PlaywrightParallelTest();
        deviceFour.executeTest(capabilitiesObject);
    }
}

class CombinationFive implements Runnable {
    public void run() {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "playwright-chromium");   // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        capabilitiesObject.addProperty("os", "osx");
        capabilitiesObject.addProperty("os_version", "catalina");
        capabilitiesObject.addProperty("name", "Chrome on Win10");
        capabilitiesObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceFive = new PlaywrightParallelTest();
        deviceFive.executeTest(capabilitiesObject);
    }
}

public class PlaywrightParallelTest {

    public static void main(String[] args) throws Exception {
        Object[] combinationObjects = { new CombinationOne(), new CombinationTwo(), new CombinationThree(), new CombinationFour(), new CombinationFive() };
        ArrayList<Thread> threadList = new ArrayList<Thread>();

        for (Object combination: combinationObjects) {
            Thread combinationThread = new Thread((Runnable) combination);
            threadList.add(combinationThread);
            combinationThread.start();
        }

        for (Thread thread: threadList) {
            thread.join();
        }
    }

    public void executeTest( JsonObject capabilitiesObject ) {
        capabilitiesObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
        capabilitiesObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");

        Page page = null;
        try (Playwright playwright = Playwright.create()) {
            BrowserType chromium = playwright.chromium();
            String caps = URLEncoder.encode(capabilitiesObject.toString(), "utf-8");
            String ws_endpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;
            Browser browser = chromium.connect(ws_endpoint);
            page = browser.newPage();
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
            browser.close();
        } catch (Exception err) {
            assert page != null;
            markTestStatus("failed", err.getMessage(), page);
        }
    }

    public static void markTestStatus(String status, String reason, Page page) {
        Object result;
        result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
        System.out.println(result);
    }
}
