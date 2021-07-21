const expect = require('chai').expect
const { chromium, devices } = require('playwright');

const cp = require('child_process');
const clientPlaywrightVersion = cp.execSync('npx playwright --version').toString().trim().split(' ')[1];

(async () => {
  /*
  * The following caps variable is for defining the BrowserStack specific capabilities
  * The test will run in the browser/os combination is specified here
  * The name of the test and also the build name goes here as well
  * The credentials also need to be part of the caps as 'browserstack.username' and 'browserstack.accessKey'
  */
  const caps = {
  	'browser': 'playwright-chromium',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'name': 'Test on Playwright emulated Pixel 5',
    'build': 'playwright-build-4',
    'browserstack.username': process.env.BROWSERSTACK_USERNAME || 'YOUR_USERNAME',
    'browserstack.accessKey': process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY',
    'client.playwrightVersion': clientPlaywrightVersion  // Playwright version being used on your local project needs to be passed in this capability for BrowserStack to be able to map request and responses correctly
  };
  const browser = await chromium.connect({
    wsEndpoint: `wss://cdp.browserstack.com/playwright?caps=${encodeURIComponent(JSON.stringify(caps))}`,
  });
  const context = await browser.newContext({...devices['Pixel 5']});  // Complete list of devices - https://github.com/microsoft/playwright/blob/master/src/server/deviceDescriptors.js
  const page = await context.newPage();
  /*
  * This is the end of BrowserStack specific code. The following lines belong to the sample test that we will run.
  */
  await page.goto('https://www.google.com/ncr');
  const element = await page.$('[aria-label="Search"]');
  await element.click();
  await element.type('BrowserStack');
  await element.press('Enter');
  const title = await page.title('');
  console.log(title);
  try {
    expect(title).to.equal("BrowserStack - Google Search", 'Expected page title is incorrect!');
    // following line of code is responsible for marking the status of the test on BrowserStack as 'passed'. You can use this code in your after hook after each test
    await page.evaluate(_ => {}, `browserstack_executor: ${JSON.stringify({action: 'setSessionStatus',arguments: {status: 'passed',reason: 'Title matched'}})}`);
  } catch {
    await page.evaluate(_ => {}, `browserstack_executor: ${JSON.stringify({action: 'setSessionStatus',arguments: {status: 'failed',reason: 'Title did not match'}})}`);
  }
  await browser.close();
})();