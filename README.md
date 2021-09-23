# playwright-browserstack
Sample Playwright tests to run on BrowserStack

## Introduction

You can now run your Playwright tests on the BrowserStack infrastructure. Porting your existing Playwright tests to run on BrowserStack, can be done in a matter of minutes.

This guide walks you through running a sample Playwright test on BrowserStack and then goes on to run tests on privately hosted websites and also shows cross-browser tests run in parallel to speed up the build execution.

## Pre-requisites

You need BrowserStack credentials to be able to run Playwright tests and also you need to be included in the Beta group so that we can enable Playwright access for you in our infra while the integration is in closed-beta.

If you have already been included in the beta group, proceed ahead. Else, you can [reach out to support](https://www.browserstack.com/contact#technical-support) to get included in the beta group.

You have to replace `YOUR_USERNAME` and `YOUR_ACCESS_KEY` in the sample scripts in this repository with your BrowserStack credentials which can be found in your [Account Settings](https://www.browserstack.com/accounts/settings) page.

**Alternatively, you can set the environment variables `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY` with your credentials and all the scripts in this repository should work fine**

## Run your first Playwright test on BrowserStack

1. Clone this repository
2. Install the dependencies using `npm install`
3. Run the sample script using `node google_search.js`

**Important Note**: When you try to run your Playwright tests on BrowserStack, you need to pass your locally installed client Playwright version in the capability `client.playwrightVersion`. This is required because it is often possible that your locally installed version might be different than the server version running on BrowserStack and the mismatch could lead to different request/response formats leading to socket errors.

Playwright does not pass the client version information in the `connect` request yet and hence this capability is required to let BrowserStack know about your locally installed version, to avoid any error scenarios arising from this. **This has already been taken care in the sample scripts under this repository**.

## Run cross-browser tests in parallel

1. Clone this repository
2. Install the dependencies using `npm install`
3. Run the `parallel_test.js` script using `node parallel_test.js`

## Run sample test on privately hosted websites

1. You have to download the BrowserStack Local binary from the links below (depending on your environment):
   * [OS X (10.7 and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip)
   * [Linux 32-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip)
   * [Linux 64-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip)
   * [Windows (XP and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-win32.zip)
2. Once you have downloaded and unzipped the file, you can initiate the binary by running the command: `./BrowserStackLocal --key YOUR_ACCESS_KEY`
3. Once you see the terminal say “\[SUCCESS\] You can now access your local server(s) in our remote browser”, your local testing connection is considered established.
4. You can then run the sample Local test using `node local_test.js`

## Supported Browsers and Playwright versions

BrowserStack Playwright tests in beta supports the following browsers across the following OS versions:

### Browsers supported
1. Bundled Chromium with Playwright (`'browser': 'playwright-chromium'`)
2. Bundled Firefox with Playwright (`'browser': 'playwright-firefox'`)
3. Bundled Webkit with Playwright (`'browser': 'playwright-webkit'`)
4. Branded Google Chrome (`'browser': 'chrome'`)
   * You can test on multiple versions of branded Google Chrome using another capability `'browser_version': 'latest'`. Supported options are `latest`, `latest-beta`, `latest-1` and so on.
   * We would be supporting Chrome v83 and above. You can also specify `browser_version` as `88` or `91` and so on.
5. Branded Microsoft Edge (`'browser': 'edge'`)
   * You can test on multiple versions of branded Microsoft Edge using another capability `'browser_version': 'latest'`. Supported options are `latest`, `latest-beta`, `latest-1` and so on.
   * We would be supporting Edge v83 and above. You can also specify `browser_version` as `88` or `91` and so on.

**The bundled browser version that will be used to run your test is the same that comes bundled with the Playwright version**. Supported Playwright versions and the option to specify them is given in a later section of this page.

**Note**: You can also run your tests in any of the mobile `devices` that Playwright supports for emulation. You can find the [sample code for running in an iPhone emulator](https://github.com/browserstack/playwright-browserstack/blob/master/sample_test_on_iPhone.js) and the [sample for running on Pixel](https://github.com/browserstack/playwright-browserstack/blob/master/sample_test_on_Pixel.js). The complete list of `devices` supported by Playwright can be found [here](https://github.com/microsoft/playwright/blob/master/src/server/deviceDescriptors.js)

### OS (with versions) supported
1. Windows 10 (`'os': 'Windows', 'os_version': '10'`)
2. macOS Big Sur (`'os': 'osx', 'os_version': 'Big Sur'`)
3. macOS Catalina (`'os': 'osx', 'os_version': 'Catalina'`)
4. macOS Mojave (`'os': 'osx', 'os_version': 'Mojave'`)

### Playwright versions supported

Currently, we are supporting 2 Playwright versions.
Playwright version can be specified using a capability for e.g.: `'browserstack.playwrightVersion': '1.12.3'`. If you do not specify a value for this capability then your tests would run on the latest version that is supported on the BrowserStack platform.

We recommend you to pass the capability value as `'browserstack.playwrightVersion': '1.latest'` or `1.latest-1` to signify the 2 latest versions within the major version `1`. If you pass this capability, then you can be rest assured that whichever are the latest versions on BrowserStack, your tests would run on that without you needing to change your test code often.

You can learn more about the significance of selecting a Playwright version on the browser version in our [documentation](https://www.browserstack.com/docs/automate/playwright/browsers-and-os).

## Get Playwright session details

While your Playwright session runs on BrowserStack, we generate a unique ID for the session, build and also generate URLs for the various types of logs which you can use to build your own reporting or for any other purpose that you might like.

A sample script with the use of the API to fetch all the relevant session details is shown in the [sample_session_details_API.js](./sample_session_details_API.js). 

You can see the [documentation for marking test status using REST API](https://www.browserstack.com/docs/automate/api-reference/selenium/session#set-test-status) using the session ID for the session, any time even after the session has completed its execution.

## Playwright with Jest

If you are using Jest to run your Playwright tests, you can run all your playwright-jest tests on BrowserStack as well. Follow the steps below to run the sample tests in this repository:

1. Clone this repository using `git clone https://github.com/browserstack/playwright-browserstack.git` (if not already done).
2. Go inside the directory playwright-jest using `cd playwright-jest`
3. Install the dependencies using `npm install`
4. Put in your credentials in the file `jest-playwright.config.js` in the capabilities part.
5. If you are trying to run your own Jest tests on BrowserStack, then you need to ensure that you have configured the `connectOptions` and `browsers` as shown in the `module.exports` of the config file.
6. Run the sample jest script using `npm test` which runs the test `google.test.js` across 3 browsers in BrowserStack serially. Your can also configure Jest to run your tests in parallel.

## Playwright with Test Runner

If you are using Playwright Test Runner to run your Playwright tests, you can run all your playwright-test tests on BrowserStack as well. Follow the steps below to run the sample tests in this repository:

1. Clone this repository using `git clone https://github.com/browserstack/playwright-browserstack.git` (if not already done).
2. Go inside the directory playwright-test using `cd playwright-test`
3. Install the dependencies using `npm install`
4. Put in your credentials in the file `fixtures.js` in the caps part.
5. If you are trying to run your own tests on BrowserStack, then you need to ensure that you have configured the `projects` correctly in `playwright.config.js` file.
6. Run the sample test script using `npm run test` which runs all the tests inside tests directory across 5 browsers in BrowserStack.
7. Run the sample test script using `npm run test:local` and add `browserstack.local:true` in the file `fixture.js` in caps part which runs all the tests inside tests directory across 5 browsers in BrowserStack.
8. If you want to run your tests locally, you just need to configure the `projects` without name `@browserstack` in `playwright.config.js` file.

## Facing issues?

If you are facing any issue with any of the above or any other issue in trying to run your Playwright tests on BrowserStack, you can [reach out to support](https://www.browserstack.com/contact#technical-support), select product as `Automate` and post your query there.