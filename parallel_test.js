const expect = require('chai').expect
const { chromium } = require('playwright');

const cp = require('child_process');
const clientPlaywrightVersion = cp.execSync('npx playwright --version').toString().trim().split(' ')[1];

const main = async (cap) => {
    cap['client.playwrightVersion'] = clientPlaywrightVersion;  // Playwright version being used on your local project needs to be passed in this capability for BrowserStack to be able to map request and responses correctly
    cap['browserstack.username'] = process.env.BROWSERSTACK_USERNAME || 'YOUR_USERNAME';
    cap['browserstack.accessKey'] = process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY';
    
    console.log("Starting test -->", cap['name']);
    const browser = await chromium.connect({
        wsEndpoint: `wss://cdp.browserstack.com/playwright?caps=${encodeURIComponent(JSON.stringify(cap))}`,
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
    await browser.close();
};

//  The following capabilities array contains the list of os/browser environments where you want to run your tests. You can choose to alter this list according to your needs
const capabilities = [
{
  	'browser': 'chrome',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'browser_version': 'latest', // this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
    'os': 'osx',
    'os_version': 'catalina',
    'name': 'Branded Google Chrome on Catalina',
    'build': 'playwright-build-2'
},
{
  'browser': 'edge',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
  'browser_version': 'latest', // this capability is valid only for branded `chrome` and `edge` browsers and you can specify any browser version like `latest`, `latest-beta`, `latest-1` and so on.
  'os': 'osx',
  'os_version': 'catalina',
  'name': 'Branded Microsoft Edge on Catalina',
  'build': 'playwright-build-2'
},
{
  	'browser': 'playwright-firefox',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'os': 'osx',
    'os_version': 'catalina',
    'name': 'Playwright firefox on Catalina',
    'build': 'playwright-build-2'
},
{
  	'browser': 'playwright-webkit',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'os': 'osx',
    'os_version': 'catalina',
    'name': 'Playwright webkit on Catalina',
    'build': 'playwright-build-2'
},
{
    'browser': 'playwright-chromium',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
    'os': 'osx',
    'os_version': 'Catalina',
    'name': 'Chrome on Win10',
    'build': 'playwright-build-2'
}]

//  The following code loops through the capabilities array defined above and runs your code against each environment that you have specified
capabilities.forEach(async (cap) => {
  await main(cap);
});