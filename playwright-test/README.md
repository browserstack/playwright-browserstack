# Testing with playwright-browserstack in Test Runner

[Playwright](https://playwright.dev/) Integration with BrowserStack.

![BrowserStack Logo](https://d98b8t1nnulk5.cloudfront.net/production/images/layout/logo-header.png?1469004780)

## Setup

* Clone the repo and run `cd playwright-test`
* Run `npm install`

## Running your tests

- To run a single test, run `npm test`

  ### Run sample test on privately hosted websites

    **Using Command-line Interface**
    1. You have to download the BrowserStack Local binary from the links below (depending on your environment):
        * [OS X (10.7 and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip)
        * [Linux 32-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip)
        * [Linux 64-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip)
        * [Windows (XP and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-win32.zip)
    2. Once you have downloaded and unzipped the file, you can initiate the binary by running the command: `./BrowserStackLocal --key YOUR_ACCESS_KEY`
    3. Once you see the terminal say "[SUCCESS]" You can now access your local server(s) in our remote browser”, your local testing connection is considered established.
    4. You can then run the sample Local test using `npm run test:local`

Understand how many parallel sessions you need by using our [Parallel Test Calculator](https://www.browserstack.com/automate/parallel-calculator?ref=github)


## Notes
* You can view your test results on the [BrowserStack Automate dashboard](https://www.browserstack.com/automate)

## Additional Resources
* [Documentation for writing Automate test scripts with BrowserStack](https://www.browserstack.com/docs/automate/playwright)
