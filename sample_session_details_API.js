// const assert = require('assert');
const expect = require('chai').expect
const { chromium } = require('playwright');

const cp = require('child_process');
const clientPlaywrightVersion = cp.execSync('npx playwright --version').toString().trim().split(' ')[1];

(async () => {
  const caps = {
  	'browser': 'playwright-chromium',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'os': 'osx',
    'os_version': 'catalina',
    'name': 'My first playwright test',
    'build': 'playwright-build-1',
    'browserstack.username': process.env.BROWSERSTACK_USERNAME || 'YOUR_USERNAME',
    'browserstack.accessKey': process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY',
    'client.playwrightVersion': clientPlaywrightVersion  // Playwright version being used on your local project needs to be passed in this capability for BrowserStack to be able to map request and responses correctly
  };
  const browser = await chromium.connect({
    wsEndpoint: `wss://cdp.browserstack.com/playwright?caps=${encodeURIComponent(JSON.stringify(caps))}`,
  });
  const page = await browser.newPage();
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

  /* 
  *  The following part of the code uses the getSessionDetails API to get all the relevant details about the running playwright session.
  *  You can use all these details after your test has completed, to fetch logs or use any other Automate REST APIs
  */
  const resp = await JSON.parse(await page.evaluate(_ => {}, `browserstack_executor: ${JSON.stringify({action: 'getSessionDetails'})}`));
  console.log(resp.hashed_id);  // This gives the session ID of the running session
  console.log(resp);  // This prints the entire JSON response. You can use any/all of the response attributes the way you like.
  
  await browser.close();
})();
