# Testing with playwright-browserstack in Java

[Playwright](https://playwright.dev/java/) Integration with BrowserStack.

![BrowserStack Logo](https://d98b8t1nnulk5.cloudfront.net/production/images/layout/logo-header.png?1469004780)

## Setup

* Clone the repo and run `cd playwright-java`

## Running your tests

- To run a single test, run 
  `mvn -Dexec.mainClass="com.browserstack.PlaywrightSingleTest" -Dexec.classpathScope=test test-compile exec:java
`
- To run parallel tests, run 
  `mvn -Dexec.mainClass="com.browserstack.PlaywrightParallelTest" -Dexec.classpathScope=test test-compile exec:java
`
- To run sessions on emulated devices, 
  ```
  mvn -Dexec.mainClass="com.browserstack.PlaywrightIPhoneTest" -Dexec.classpathScope=test test-compile exec:java
  ``` 
  or 
  ```
  mvn -Dexec.mainClass="com.browserstack.PlaywrightPixelTest" -Dexec.classpathScope=test test-compile exec:java
  ```
You can specify contextOptions() from the below list: 
https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json
- Run `mvn -Dexec.mainClass="com.browserstack.PlaywrightSessionDetailsTest" -Dexec.classpathScope=test test-compile exec:java` to check how to get session details.

  ### Run sample test on privately hosted websites

  **Using Language Bindings**
    1. Run 
    `mvn -Dexec.mainClass="com.browserstack.PlaywrightLocalUsingBindingsTest" -Dexec.classpathScope=test test-compile exec:java`

  **Using Command-line Interface**

  1. You have to download the BrowserStack Local binary from the links below (depending on your environment):
      * [OS X (10.7 and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip)
      * [Linux 32-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip)
      * [Linux 64-bit](https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip)
      * [Windows (XP and above)](https://www.browserstack.com/browserstack-local/BrowserStackLocal-win32.zip)
  2. Once you have downloaded and unzipped the file, you can initiate the binary by running the command: `./BrowserStackLocal --key YOUR_ACCESS_KEY`
  3. Once you see the terminal say "[SUCCESS]" You can now access your local server(s) in our remote browser”, your local testing connection is considered established.
  4. You can then run the sample Local test using 
    `mvn -Dexec.mainClass="com.browserstack.PlaywrightLocalTest" -Dexec.classpathScope=test test-compile exec:java`

Understand how many parallel sessions you need by using our [Parallel Test Calculator](https://www.browserstack.com/automate/parallel-calculator?ref=github)


## Notes
* You can view your test results on the [BrowserStack Automate dashboard](https://www.browserstack.com/automate)

## Additional Resources
* [Documentation for writing Automate test scripts with BrowserStack](https://www.browserstack.com/docs/automate/playwright)
