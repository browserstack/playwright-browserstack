using Microsoft.Playwright;
using System.Threading.Tasks;
using System;
using System.Collections.Generic;
using Newtonsoft.Json;

class PlaywrightSingleTest
{
    public static async Task main(string[] args)
    {
        using var playwright = await Playwright.CreateAsync();

        Dictionary<string, string> browserstackOptions = new Dictionary<string, string>();
        browserstackOptions.Add("name", "Playwright first sample test");
        browserstackOptions.Add("build", "playwright-build-1");
        browserstackOptions.Add("os", "osx");
        browserstackOptions.Add("os_version", "catalina");
        browserstackOptions.Add("browser", "chrome");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        browserstackOptions.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        browserstackOptions.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
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
            await MarkTestStatus("passed", "Title matched", page);
          }
          else {
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
