package com.browserstack;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

class DeviceOne implements Runnable {
    public void run() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        jsonObject.addProperty("browser_version", "latest");
        jsonObject.addProperty("os", "osx");
        jsonObject.addProperty("os_version", "catalina");
        jsonObject.addProperty("name", "Branded Google Chrome on Catalina");
        jsonObject.addProperty("build", "playwright-build-2");

        PlaywrightParallelTest deviceOne = new PlaywrightParallelTest();
        deviceOne.executeTest(jsonObject);
    }
}

class DeviceTwo implements Runnable {
    public void run() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browser", "edge");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        jsonObject.addProperty("browser_version", "latest");
        jsonObject.addProperty("os", "osx");
        jsonObject.addProperty("os_version", "catalina");
        jsonObject.addProperty("name", "Branded Microsoft Edge on Catalina");
        jsonObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceTwo = new PlaywrightParallelTest();
        deviceTwo.executeTest(jsonObject);
    }
}

class DeviceThree implements Runnable {
    public void run() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browser", "playwright-firefox");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        jsonObject.addProperty("os", "osx");
        jsonObject.addProperty("os_version", "catalina");
        jsonObject.addProperty("name", "Playwright firefox on Catalina");
        jsonObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceThree = new PlaywrightParallelTest();
        deviceThree.executeTest(jsonObject);
    }
}

class DeviceFour implements Runnable {
    public void run() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browser", "playwright-webkit"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        jsonObject.addProperty("os", "osx");
        jsonObject.addProperty("os_version", "catalina");
        jsonObject.addProperty("name", "Playwright webkit on Catalina");
        jsonObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceFour = new PlaywrightParallelTest();
        deviceFour.executeTest(jsonObject);
    }
}

class DeviceFive implements Runnable {
    public void run() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("browser", "playwright-chromium");   // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        jsonObject.addProperty("os", "osx");
        jsonObject.addProperty("os_version", "catalina");
        jsonObject.addProperty("name", "Chrome on Win10");
        jsonObject.addProperty("build", "playwright-build-2");
        PlaywrightParallelTest deviceFive = new PlaywrightParallelTest();
        deviceFive.executeTest(jsonObject);
    }
}

public class PlaywrightParallelTest {

    public static void main(String[] args) throws Exception {
        Thread threadOne = new Thread(new DeviceOne());
        threadOne.start();
        Thread threadTwo = new Thread(new DeviceTwo());
        threadTwo.start();
        Thread threadThree = new Thread(new DeviceThree());
        threadThree.start();
        Thread threadFour = new Thread(new DeviceFour());
        threadFour.start();
        Thread threadFive = new Thread(new DeviceFive());
        threadFive.start();
    }

    public void executeTest( JsonObject jsonObject ) {
        jsonObject.addProperty("browserstack.username", "BROWSERSTACK_USERNAME");
        jsonObject.addProperty("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");

        try (Playwright playwright = Playwright.create()) {
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

            Object result;
            if (title.equals("BrowserStack - Google Search")) {
                // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
                result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"passed\", \"reason\": \"Title matched\"}}");
                System.out.println(result);
            } else {
                result = page.evaluate("_ => {}", "browserstack_executor: { \"action\": \"setSessionStatus\", \"arguments\": { \"status\": \"failed\", \"reason\": \"Title did not matched\"}}");
                System.out.println(result);
            }
            browser.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
    }
}
