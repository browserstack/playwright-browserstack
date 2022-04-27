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
    print("BrowserStackLocal running: " + str(bs_local.isRunning()))

    clientPlaywrightVersion = str(subprocess.getoutput('playwright --version')).strip().split(" ")[1]
    desired_cap['client.playwrightVersion'] = clientPlaywrightVersion

    try:
      cdpUrl = 'wss://cdp.browserstack.com/playwright?caps=' + urllib.parse.quote(json.dumps(desired_cap))
      browser = playwright.chromium.connect(cdpUrl)
      page = browser.new_page()
      try:
        page.goto("http://localhost:45691")
        page.main_frame.wait_for_function("""
          document
            .querySelector("body")
            .innerText
            .includes("This is an internal server for BrowserStack Local")
        """)
        mark_test_status("passed", "Local is up and running", page)
      except Exception:
        mark_test_status("failed", "BrowserStack Local binary is not running", page)
      browser.close()
    except Exception as ex:
      print("Exception while creating page context: ", str(ex))
    finally:
      # Stop the Local instance
      bs_local.stop()
      print("BrowserStackLocal stopped")

def mark_test_status(status, reason, page):
  page.evaluate("_ => {}", "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\""+ status + "\", \"reason\": \"" + reason + "\"}}");

with sync_playwright() as playwright:
  run_local_session(playwright)
