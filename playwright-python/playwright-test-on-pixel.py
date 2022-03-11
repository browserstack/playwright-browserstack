import json
import urllib
import subprocess
from playwright.sync_api import sync_playwright

desired_cap = {
  'browser': 'playwright-chromium',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', # this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'name': 'Test on Playwright emulated Pixel 5',
  'build': 'playwright-python-4',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
}

def run_session(playwright):
  clientPlaywrightVersion = str(subprocess.getoutput('playwright --version')).strip().split(" ")[1]
  desired_cap['client.playwrightVersion'] = clientPlaywrightVersion

  cdpUrl = 'wss://cdp.browserstack.com/playwright?caps=' + urllib.parse.quote(json.dumps(desired_cap))
  pixel = playwright.devices["Pixel 5"]   # // Complete list of devices - https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json
  browser = playwright.chromium.connect(cdpUrl)
  context = browser.new_context(**pixel)
  page = context.new_page()
  try:
    page.goto("https://www.google.co.in/")
    page.fill("[aria-label='Search']", 'Browserstack')
    page.keyboard.press('Enter')
    page.wait_for_timeout(1000)
    title = page.title()

    if title == "Browserstack - Google Search":
      # following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
      mark_test_status("passed", "Title matched", page)
    else:
      mark_test_status("failed", "Title did not match", page)
  except Exception as err:
      mark_test_status("failed", str(err), page)
  
  browser.close()

def mark_test_status(status, reason, page):
  page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\""+ status + "\", \"reason\": \"" + reason + "\"}}");

with sync_playwright() as playwright:
  run_session(playwright)

