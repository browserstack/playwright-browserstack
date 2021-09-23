// example.spec.js
const { test } = require('../fixtures');
const { expect } = require('@playwright/test');
test.describe('feature foo', () => {
  test('my test', async ({ page }) => {
    // Assertions use the expect API.
    await page.goto('https://www.google.com/ncr');
    const element = await page.$('[aria-label="Search"]');
    await element.click();
    await element.type('BrowserStack');
    await element.press('Enter');
    const title = await page.title('');
    console.log(title);
    expect(title).toEqual( 'BrowserStack - Google Search', 'Expected page title is incorrect!');
  });
});
