# Testing with playwright-browserstack in Python

[Playwright](https://playwright.dev/python/) Integration with BrowserStack.

![BrowserStack Logo](https://d98b8t1nnulk5.cloudfront.net/production/images/layout/logo-header.png?1469004780)

## Setup

* Clone the repo and run `cd playwright-python`
* Make sure you have Python 3 (version < 3.9.0) installed on your system
* Install Playwright
  ```
  pip install --upgrade pip 
  pip install playwright
  playwright install
  ```

## Running your tests

- To run a single test, run `python single-playwright-test.py`
- To run parallel tests, run `python parallel-playwright-test.py`
- To run sessions on emulated devices, 
`python playwright-test-on-iphone.py` or `python playwright-test-on-pixel.py`
You can specify any device name from thr below list: 
https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json
- Run `python session-details-playwright-test.py` to check how to get session details.

  ### Run sample test on privately hosted websites
  **Using Language Bindings**
    1. Follow the steps below:
       ```
       pip install browserstack-local
       python local-using-bindings-playwright-test.py
       ```

    **Using Command-line Interface**

  1. You have to download the BrowserStack Local binary from the links below (depending on your environment):
      * [OS X (10.7 and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip)
      * [Linux 32-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip)
      * [Linux 64-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip)
      * [Windows (XP and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-win32.zip)
  2. Once you have downloaded and unzipped the file, you can initiate the binary by running the command: `./BrowserStackLocal --key YOUR_ACCESS_KEY`
  3. Once you see the terminal say "[SUCCESS]" You can now access your local server(s) in our remote browser”, your local testing connection is considered established.
  4. You can then run the sample Local test using `python local-playwright-test.py`

Understand how many parallel sessions you need by using our [Parallel Test Calculator](https://www.browserstack.com/automate/parallel-calculator?ref=github)


## Notes
* You can view your test results on the [BrowserStack Automate dashboard](https://www.browserstack.com/automate)

## Additional Resources
* [Documentation for writing Automate test scripts with BrowserStack](https://www.browserstack.com/docs/automate/playwright)
