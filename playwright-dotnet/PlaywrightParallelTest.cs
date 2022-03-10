using Microsoft.Playwright;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Collections.Generic;
using Newtonsoft.Json;

class PlaywrightParallelTest
{
    public static async Task main(string[] args)
    {
        //  The following capability variables contains the set of os/browser environments where you want to run your tests. You can choose to alter this list according to your needs. Read more on https://browserstack.com/docs/automate/playwright/browsers-and-os
        try {
            // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
            // browser_version capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
            var combination1 = SampleTestCase("playwright-chromium", "latest", "osx", "Catalina", "macOS Catalina - Chrome latest", "Parallel-build-2"); 
            var combination2 = SampleTestCase("chrome", "latest", "osx", "catalina", "Branded Google Chrome on Catalina", "Parallel-build-2");
            var combination3 = SampleTestCase("edge", "latest", "osx", "catalina", "Branded Microsoft Edge on Catalina", "Parallel-build-2");
            var combination4 = SampleTestCase("playwright-firefox", "latest", "osx", "catalina", "Playwright firefox on Catalina", "Parallel-build-2");
            var combination5 = SampleTestCase("playwright-webkit", "latest", "osx", "catalina", "Playwright webkit on Catalina", "Parallel-build-2");
            
            //Executing the methods
            await Task.WhenAll(combination1, combination2, combination3, combination4, combination5);
        } catch (Exception e) {
            Console.WriteLine(e);
        }
    }
    static async Task SampleTestCase(String browser_name, String browser_version, String os, String os_version, String test_name, String build_name)
    {
        Dictionary<string, string> browserstackOptions = new Dictionary<string, string>();
        string capsJson;

        try {
            browserstackOptions.Add("build", build_name);
            browserstackOptions.Add("name", test_name);
            browserstackOptions.Add("os", os);
            browserstackOptions.Add("os_version", os_version);
            browserstackOptions.Add("browser", browser_name);
            browserstackOptions.Add("browser_version", browser_version);
            browserstackOptions.Add("browserstack.username", "BROWSERSTACK_USERNAME");
            browserstackOptions.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
            capsJson = JsonConvert.SerializeObject(browserstackOptions);
            var task = Executetestwithcaps(capsJson);
            await task;
        } catch (Exception e) {
            Console.WriteLine(e);
        }
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
            Console.WriteLine(err);
            await MarkTestStatus("failed", err.Message, page);
        }
        await browser.CloseAsync();
    }
    public static async Task MarkTestStatus(string status, string reason, IPage page) {
        await page.EvaluateAsync("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"" + status + "\", \"reason\": \"" + reason + "\"}}");
    }
}
