// global-teardown.js
const { bsLocal } = require('./fixtures');
let localStopped = false;
const {promisify} = require('util');
const sleep = promisify(setTimeout);
module.exports = async (config) => {
  // Stop the Local instance after your test run is completed, i.e after driver.quit
  bsLocal.stop(() => {
    localStopped = true;
    console.log('Stopped BrowserStackLocal')
  });
  while(!localStopped){
    await sleep(1000);
  }
};
