import json
import urllib
from playwright.sync_api import sync_playwright

desired_cap = {
  'browser': 'chrome',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', # this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Branded Google Chrome on Catalina',
  'build': 'playwright-build-1',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
}

with sync_playwright() as playwright:
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
      page.evaluate("_ => {}", "browserstack_executor: {\"action\":\"setSessionStatus\",\"arguments\":{\"status\":\"passed\",\"reason\":\"Title matched\"}}");
  except:
      page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"failed\", \"reason\": \" Title did not match\"}}");
  
  browser.close() 



  