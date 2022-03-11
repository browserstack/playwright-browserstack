import json
import subprocess
import urllib
from threading import Thread
from playwright.sync_api import sync_playwright

desired_cap = [
{
  'browser': 'chrome',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', # this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Branded Google Chrome on Catalina',
  'build': 'playwright-python-2',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
},
{
  'browser': 'edge',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', # this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Branded Microsoft Edge on Catalina',
  'build': 'playwright-python-2',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
},
{
  'browser': 'playwright-firefox',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Playwright firefox on Catalina',
  'build': 'playwright-python-2',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
},
{
  'browser': 'playwright-webkit',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Playwright webkit on Catalina',
  'build': 'playwright-python-2',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
},
{
  'browser': 'playwright-chromium',  # allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'os': 'osx',
  'os_version': 'Catalina',
  'name': 'Chrome on Win10',
  'build': 'playwright-python-2',
  'browserstack.username': 'BROWSERSTACK_USERNAME',
  'browserstack.accessKey': 'BROWSERSTACK_ACCESS_KEY'
}]

def run_parallel_session(desired_cap):
  with sync_playwright() as playwright:
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

def mark_test_status(status, reason, page):
    page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\""+ status + "\", \"reason\": \"" + reason + "\"}}");

threads = []
for cap in desired_cap:
  combination_thread = Thread(target=run_parallel_session, args=(cap,))
  threads.append(combination_thread)
  combination_thread.start()

for thread in threads:
  thread.join()


