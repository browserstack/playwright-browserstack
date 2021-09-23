// global-setup.js
const { bsLocal, BS_LOCAL_ARGS } = require('./fixtures');
let localStarted = false;
const { promisify } = require('util');
const sleep = promisify(setTimeout);
module.exports = async (config) => {
  console.log('Starting BrowserStackLocal ...');
  // Starts the Local instance with the required arguments
  bsLocal.start(BS_LOCAL_ARGS, (callback) => {
    console.log('BrowserStackLocal Started');
    localStarted = true;
  });
  while (!localStarted) {
    await sleep(1000);
  }
};
