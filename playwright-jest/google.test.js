// Needs to be higher than the default Playwright timeout
jest.setTimeout(40 * 1000)

describe("Google", () => {
  failedCount = 0;
  beforeAll(async () => {
    await page.goto('https://google.com')
  })
  it('title should match BrowserStack - Google Search', async () => {
    await page.type('input[name="q"]', "BrowserStack");
    await page.press('input[name="q"]', "Enter");
    expect(await page.title()).toBe('BrowserStack - Google Search');
    await page.evaluate(_ => {}, `browserstack_executor: ${JSON.stringify({action: 'setSessionStatus',arguments: {status: 'passed',reason: 'Test assertion passed'}})}`);
  })
})