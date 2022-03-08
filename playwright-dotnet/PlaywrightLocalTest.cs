using Microsoft.Playwright;
using System.Threading.Tasks;
using System;
using System.Collections.Generic;
using Newtonsoft.Json;

class PlaywrightLocalTest
{
    public static async Task main(string[] args)
    {
        using var playwright = await Playwright.CreateAsync();

        Dictionary<string, string> browserstackOptions = new Dictionary<string, string>();
        browserstackOptions.Add("name", "Playwright local sample test");
        browserstackOptions.Add("build", "playwright-build-3");
        browserstackOptions.Add("os", "osx");
        browserstackOptions.Add("os_version", "catalina");
        browserstackOptions.Add("browser", "chrome");   // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        browserstackOptions.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        browserstackOptions.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        browserstackOptions.Add("browserstack.local", "true");
        string capsJson = JsonConvert.SerializeObject(browserstackOptions);
        string cdpUrl = "wss://cdp.browserstack.com/playwright?caps=" + Uri.EscapeDataString(capsJson);

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
            await page.EvaluateAsync("_ => {}", "browserstack_executor: {\"action\":\"setSessionStatus\",\"arguments\":{\"status\":\"passed\",\"reason\":\"Title matched\"}}");
          }
        }
        catch {
          await page.EvaluateAsync("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"failed\", \"reason\": \" Title did not match\"}}");
        }
        await browser.CloseAsync();
    }
}
