const expect = require('chai').expect
const { chromium } = require('playwright');

const cp = require('child_process');
const clientPlaywrightVersion = cp.execSync('npx playwright --version').toString().trim().split(' ')[1];

const { bootstrap } = require('global-agent');

// Have to set this environment variable with required data before executing this script 
// export GLOBAL_AGENT_HTTP_PROXY=http://someuser:test123@127.0.0.1:3128

bootstrap();

(async () => {
    const caps = {
        'browser': 'chrome',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
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
    await browser.close();
  })();