const { chromium } = require('playwright');
const BrowserStackLocal = require('browserstack-local');

const cp = require('child_process');
const clientPlaywrightVersion = cp.execSync('npx playwright --version').toString().trim().split(' ')[1];

const bsLocal = new BrowserStackLocal.Local();

// replace YOUR_ACCESS_KEY with your key. You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
const BS_LOCAL_ARGS = {
    'key': process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY'
};

/**
 * Mark test status on BrowserStack.
 *
 * @param {Page} page - Page object created by Puppeteer context.
 * @param {String} status - Status string can be either passed|failed.
 * @param {String} reason - Explanatory reason for the status marked.
 * @return {Promise<String>} Stringified response from BrowserStack regarding the
 * execution of the jsExecutor.
 */
function markTest(page, status, reason) {
    return page.evaluate(
        _ => {},
        `browserstack_executor: ${JSON.stringify({
            action: 'setSessionStatus',
            arguments: { status, reason }
        })}`);
}

/**
 * Driver Test Function.
 *
 * @async
 * @return {Promise<void>}
 */
async function testFn() {
    console.log('Started BrowserStackLocal');

    // Check if BrowserStack local instance is running
    console.log(`BrowserStackLocal running: ${bsLocal.isRunning()}`);

    const caps = {
        'browser': 'edge',  // allowed browsers are `chrome`, `edge`, `playwright-chromium`, `playwright-firefox` and `playwright-webkit`
        'os': 'osx',
        'os_version': 'catalina',
        'name': 'Playwright sample Local test',
        'build': 'playwright-build-3',
        'browserstack.local': 'true',
        'browserstack.username': process.env.BROWSERSTACK_USERNAME || 'YOUR_USERNAME',
        'browserstack.accessKey': process.env.BROWSERSTACK_ACCESS_KEY || 'YOUR_ACCESS_KEY',
        'client.playwrightVersion': clientPlaywrightVersion  // Playwright version being used on your local project needs to be passed in this capability for BrowserStack to be able to map request and responses correctly
    };

    // Use `.connect()` to initiate an Automate session on BrowserStack
    const browser = await chromium.connect({
        wsEndpoint: `wss://cdp.browserstack.com/playwright?caps=${encodeURIComponent(JSON.stringify(caps))}`,
      });
    // BrowserStack specific code ends here

    const page = await browser.newPage();
    await page.goto('http://localhost:45691');
    try {
        await page.waitForFunction(
            `document
                .querySelector("body")
                .innerText
                .includes("This is an internal server for BrowserStack Local")`,
        );
        // Following line of code is responsible for marking the status of the
        // test on BrowserStack as 'passed'. You can use this code in your
        // after hook after each test
        await markTest(page, 'passed', 'Local is up and running');
    } catch {
        await markTest(page, 'failed', 'BrowserStack Local binary is not running');
    }
    await browser.close();

    // Stop the Local instance after your test run is completed, i.e after driver.quit
    bsLocal.stop(() => console.log('Stopped BrowserStackLocal'));
}

// Starts the Local instance with the required arguments
bsLocal.start(BS_LOCAL_ARGS, testFn);
