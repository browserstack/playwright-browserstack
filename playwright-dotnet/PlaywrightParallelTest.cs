using Microsoft.Playwright;
using System;
using System.Threading;
using Newtonsoft.Json;
using System.Collections;

class PlaywrightParallelTest
{
    public static async Task main(string[] args)
    {
        //  The following capability variables contains the set of os/browser environments where you want to run your tests. You can choose to alter this list according to your needs. Read more on https://browserstack.com/docs/automate/playwright/browsers-and-os
        try {
            ArrayList capabilitiesList = getCapabilitiesList();
            Task[] taskList = new Task[capabilitiesList.Count]; 

            for (int i = 0; i < capabilitiesList.Count; i++)
            {
                string capsJson;
                capsJson = JsonConvert.SerializeObject(capabilitiesList[i]);
                var task = Executetestwithcaps(capsJson);
                taskList[i] = task;
            }

            await Task.WhenAll(taskList);

        } catch (Exception e) {
            Console.WriteLine(e);
        }
    }
    static ArrayList getCapabilitiesList()
    {
        ArrayList capabilitiesList = new ArrayList(); 

        Dictionary<string, string> catalinaChromeCap = new Dictionary<string, string>();
        catalinaChromeCap.Add("browser", "chrome");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaChromeCap.Add("browser_version", "latest");
        catalinaChromeCap.Add("os", "osx");
        catalinaChromeCap.Add("os_version", "catalina");
        catalinaChromeCap.Add("name", "Branded Google Chrome on Catalina");
        catalinaChromeCap.Add("build", "playwright-dotnet-2");
        catalinaChromeCap.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        catalinaChromeCap.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        capabilitiesList.Add(catalinaChromeCap);

        Dictionary<string, string> catalinaEdgeCap = new Dictionary<string, string>();
        catalinaEdgeCap.Add("browser", "edge");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaEdgeCap.Add("browser_version", "latest");
        catalinaEdgeCap.Add("os", "osx");
        catalinaEdgeCap.Add("os_version", "catalina");
        catalinaEdgeCap.Add("name", "Branded Microsoft Edge on Catalina");
        catalinaEdgeCap.Add("build", "playwright-dotnet-2");
        catalinaEdgeCap.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        catalinaEdgeCap.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        capabilitiesList.Add(catalinaEdgeCap);

        Dictionary<string, string> catalinaFirefoxCap = new Dictionary<string, string>();
        catalinaFirefoxCap.Add("browser", "playwright-firefox");    // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaFirefoxCap.Add("os", "osx");
        catalinaFirefoxCap.Add("os_version", "catalina");
        catalinaFirefoxCap.Add("name", "Playwright firefox on Catalina");
        catalinaFirefoxCap.Add("build", "playwright-dotnet-2");
        catalinaFirefoxCap.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        catalinaFirefoxCap.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        capabilitiesList.Add(catalinaFirefoxCap);

        Dictionary<string, string> catalinaWebkitCap = new Dictionary<string, string>();
        catalinaWebkitCap.Add("browser", "playwright-webkit"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaWebkitCap.Add("os", "osx");
        catalinaWebkitCap.Add("os_version", "catalina");
        catalinaWebkitCap.Add("name", "Playwright webkit on Catalina");
        catalinaWebkitCap.Add("build", "playwright-dotnet-2");
        catalinaWebkitCap.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        catalinaWebkitCap.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        capabilitiesList.Add(catalinaWebkitCap);

        Dictionary<string, string> catalinaChromiumCap = new Dictionary<string, string>();
        catalinaChromiumCap.Add("browser", "playwright-chromium"); // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        catalinaChromiumCap.Add("os", "osx");
        catalinaChromiumCap.Add("os_version", "catalina");
        catalinaChromiumCap.Add("name", "Playwright webkit on Catalina");
        catalinaChromiumCap.Add("build", "playwright-dotnet-2");
        catalinaChromiumCap.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        catalinaChromiumCap.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        capabilitiesList.Add(catalinaChromiumCap);

        return capabilitiesList;
    }

    //Executetestwithcaps function takes capabilities from 'SampleTestCase' function and executes the test
    public static async Task Executetestwithcaps(string capabilities)
    {
        using var playwright = await Playwright.CreateAsync();
        string cdpUrl = "wss://cdp.browserstack.com/playwright?caps=" + Uri.EscapeDataString(capabilities);

        await using var browser = await playwright.Chromium.ConnectAsync(cdpUrl);
        var page = await browser.NewPageAsync();
        try {
            await page.GotoAsync("https://www.google.co.in/");
            await page.Locator("[aria-label='Search']").ClickAsync();
            await page.FillAsync("[aria-label='Search']", "BrowserStack");
            await page.Locator("[aria-label='Google Search'] >> nth=0").ClickAsync();
            var title = await page.TitleAsync();

            if (title == "BrowserStack - Google Search")
            {
                // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
                await MarkTestStatus("passed", "Title matched", page);
            } else
            {
                await MarkTestStatus("failed", "Title did not match", page);
            }
        }
        catch (Exception err) {
            await MarkTestStatus("failed", err.Message, page);
        }
        await browser.CloseAsync();
    }
    public static async Task MarkTestStatus(string status, string reason, IPage page) {
        await page.EvaluateAsync("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"" + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
