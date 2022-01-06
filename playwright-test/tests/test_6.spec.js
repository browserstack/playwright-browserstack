const { test } = require('../fixtures');
const { expect } = require('@playwright/test');
test.describe('feature foo', () => {
  test('test 6', async ({ page }) => {
    // Assertions use the expect API.
    await page.goto('https://www.duckduckgo.com');
    const element = await page.$('[name="q"]');
    await element.click();
    await element.type('BrowserStack Selenium');
    await element.press('Enter');
    const title = await page.title('');
    console.log(title);
    expect(title).toEqual( 'BrowserStack Selenium at DuckDuckGo', 'Expected page title is incorrect!');
  });
});
