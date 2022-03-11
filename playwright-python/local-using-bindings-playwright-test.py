import json
import urllib
import subprocess
from playwright.sync_api import sync_playwright
from browserstack.local import Local

desired_cap = {
  'browser': 'chrome',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', # this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Branded Google Chrome on Catalina',
  'build': 'playwright-python-3',
  'browserstack.local': 'true',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
}

def run_local_session(playwright):
    # Creates an instance of Local
    bs_local = Local()

    # You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
    bs_local_args = { "key": "BROWSERSTACK_ACCESS_KEY" }

    # Starts the Local instance with the required arguments
    bs_local.start(**bs_local_args)

    # Check if BrowserStack local instance is running
    print(bs_local.isRunning())

    clientPlaywrightVersion = str(subprocess.getoutput('playwright --version')).strip().split(" ")[1]
    desired_cap['client.playwrightVersion'] = clientPlaywrightVersion
    
    cdpUrl = 'wss://cdp.browserstack.com/playwright?caps=' + urllib.parse.quote(json.dumps(desired_cap))
    browser = playwright.chromium.connect(cdpUrl)
    page = browser.new_page()
    try:
      page.goto("https://www.google.co.in/")
      page.fill("[aria-label='Search']", 'Browserstack')
      locator = page.locator("[aria-label='Google Search'] >> nth=0")
      locator.click()
      title = page.title()

      if title == "Browserstack - Google Search":
        # following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
        mark_test_status("passed", "Title matched", page)
      else:
        mark_test_status("failed", "Title did not match", page)
    except Exception as err:
        mark_test_status("failed", str(err), page)
    
    browser.close()
    
    # Stop the Local instance
    bs_local.stop()

def mark_test_status(status, reason, page):
  page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\""+ status + "\", \"reason\": \"" + reason + "\"}}");

with sync_playwright() as playwright:
  run_local_session(playwright)
