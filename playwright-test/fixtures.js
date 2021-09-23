const base = require('@playwright/test');
const cp = require('child_process');
const clientPlaywrightVersion = cp
  .execSync('npx playwright --version')
  .toString()
  .trim()
  .split(' ')[1];
const BrowserStackLocal = require('browserstack-local');

const caps = {
  browser: 'chrome',
  os: 'osx',
  os_version: 'catalina',
  name: 'My first playwright test',
  build: 'playwright-build-1',
  'browserstack.username': process.env.BROWSERSTACK_USERNAME || 'YOUR_USERNAME',
  'browserstack.accessKey':
    process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY',
  'client.playwrightVersion': clientPlaywrightVersion,
};

exports.bsLocal = new BrowserStackLocal.Local();

// replace YOUR_ACCESS_KEY with your key. You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
exports.BS_LOCAL_ARGS = {
  key: process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY',
};

const patchCaps = (name) => {
  let combination = name.split(/@browserstack/)[0];
  let [browerCaps, osCaps] = combination.split(/:/);
  let [browser, browser_version] = browerCaps.split(/@/);
  let osCapsSplit = osCaps.split(/ /);
  let os = osCapsSplit.shift();
  let os_version = osCapsSplit.join(' ');
  caps.browser = browser ? browser : 'chrome';
  caps.browser_version = browser_version ? browser_version : 'latest';
  caps.os = os ? os : 'osx';
  caps.os_version = os_version ? os_version : 'catalina';
  caps.name = name;
};

exports.test = base.test.extend({
  browser: async ({ playwright, browser }, use, workerInfo) => {
    if (workerInfo.project.name.match(/browserstack/)) {
      patchCaps(workerInfo.project.name);
      const vBrowser = await playwright.chromium.connect({
        wsEndpoint:
          `wss://cdp.browserstack.com/playwright?caps=` +
          `${encodeURIComponent(JSON.stringify(caps))}`,
      });
      await use(vBrowser);
    } else {
      await use(browser);
    }
  },
  page: async ({ page, browser }, use, testInfo) => {
    if (testInfo.project.name.match(/browserstack/)) {
      const vPage = await browser.newPage();
      await use(vPage);
      const testResult = {
        action: 'setSessionStatus',
        arguments: {
          status: testInfo.status,
          reason: testInfo?.error?.message,
        },
      };
      await vPage.evaluate(() => {},
      `browserstack_executor: ${JSON.stringify(testResult)}`);
    } else {
      use(page);
    }
  },
});
