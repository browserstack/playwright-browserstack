using Microsoft.Playwright;
using System.Threading.Tasks;
using System;
using System.Collections.Generic;
using Newtonsoft.Json;

class PlaywrightIPhoneTest
{
    public static async Task main(string[] args)
    {
        using var playwright = await Playwright.CreateAsync();

        Dictionary<string, string> browserstackOptions = new Dictionary<string, string>();
        browserstackOptions.Add("name", "Test on Playwright emulated IPhone 11 Pro");
        browserstackOptions.Add("build", "playwright-dotnet-4");
        browserstackOptions.Add("browser", "playwright-webkit");  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        browserstackOptions.Add("browserstack.username", "BROWSERSTACK_USERNAME");
        browserstackOptions.Add("browserstack.accessKey", "BROWSERSTACK_ACCESS_KEY");
        string capsJson = JsonConvert.SerializeObject(browserstackOptions);
        string cdpUrl = "wss://cdp.browserstack.com/playwright?caps=" + Uri.EscapeDataString(capsJson);

        await using var browser = await playwright.Chromium.ConnectAsync(cdpUrl);
        
        var context = await browser.NewContextAsync(playwright.Devices["iPhone 11 Pro"]); // Complete list of devices - https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json

        var page = await context.NewPageAsync();
        try {
          await page.GotoAsync("https://www.google.co.in/");
          await page.Locator("[aria-label='Search']").ClickAsync();
          await page.FillAsync("[aria-label='Search']", "BrowserStack");
          await page.Keyboard.PressAsync("Enter");
          await page.Locator("[aria-current='page']").WaitForAsync();
          var title = await page.TitleAsync();

          if (title == "BrowserStack - Google Search")
          {
            // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
            await MarkTestStatus("passed", "Title matched", page);
          } else {
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
