var path = require('path'),
    http = require('http'),
    fs = require('fs-extra');

function JarBinary(filename) {
  'use strict';

  var self = this;
  self.path = filename || path.resolve(path.join(__dirname, '..', 'bin', 'jar', 'BrowserStackTunnel.jar'));
  self.command = 'java';
  self.args = ['-jar', self.path];

  self.update = function (callback) {
    fs.createFileSync(self.path);
    var jarFileStream = fs.createWriteStream(self.path);
    http.get('http://www.browserstack.com/BrowserStackTunnel.jar', function (response) {
      console.log('BrowserStackTunnel: download jar file ...');
      jarFileStream.on('finish', function () {
        console.log('BrowserStackTunnel: download complete');
        jarFileStream.close();
        callback();
      });
      response.pipe(jarFileStream);
    });
  };
}

module.exports = JarBinary;
