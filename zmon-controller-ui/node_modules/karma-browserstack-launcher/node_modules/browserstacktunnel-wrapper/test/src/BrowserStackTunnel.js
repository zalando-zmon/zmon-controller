var expect = require('expect.js'),
    mocks = require('mocks'),
    childProcessMock = require('../lib/mocks').childProcessMock,
    httpMock = require('../lib/mocks').httpMock,
    fsMock = require('../lib/mocks').fsMock,
    unzipMock = require('../lib/mocks').unzipMock,
    osMock = require('../lib/mocks').osMock,
    sinon = require('sinon');

var spawnSpy = sinon.spy(childProcessMock.spawn);
childProcessMock.spawn = spawnSpy;

var jb = mocks.loadFile('./src/JarBinary.js', {
  http: httpMock,
  'fs-extra': fsMock
});
var JarBinary = jb.JarBinary;

var zb = mocks.loadFile('./src/ZipBinary.js', {
  https: httpMock,
  fs: fsMock,
  unzip: unzipMock
});
var ZipBinary = zb.ZipBinary;

var bs = mocks.loadFile('./src/BrowserStackTunnel.js', {
  child_process: childProcessMock,
  http: httpMock,
  fs: fsMock,
  os: osMock,
  './JarBinary': JarBinary,
  './ZipBinary': ZipBinary
});

var NEW_BINARY_DIR = '/bin/new',
    NEW_BINARY_FILE = NEW_BINARY_DIR + '/BrowserStackLocal',
    JAR_FILE = '/bin/BrowserStackTunnel.jar',
    NEW_JAR_FILE = '/bin/new/BrowserStackTunnel.jar',
    OSX_BINARY_DIR = '/bin/darwin',
    OSX_BINARY_FILE = OSX_BINARY_DIR + '/BrowserStackLocal',
    LINUX_64_BINARY_DIR = '/bin/linux64',
    LINUX_64_BINARY_FILE = LINUX_64_BINARY_DIR + '/BrowserStackLocal',
    LINUX_32_BINARY_DIR = '/bin/linux32',
    LINUX_32_BINARY_FILE = LINUX_32_BINARY_DIR + '/BrowserStackLocal',
    JAR_URL = 'http://www.browserstack.com/BrowserStackTunnel.jar',
    OSX_BINARY_URL = 'https://www.browserstack.com/browserstack-local/BrowserStackLocal-darwin-x64.zip',
    LINUX_64_BINARY_URL = 'https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip',
    LINUX_32_BINARY_URL = 'https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-ia32.zip',
    HOST_NAME = 'localhost',
    PORT = 8080,
    INVALID_PORT = 8081,
    SSL_FLAG = 0,
    KEY = 'This is a fake key',
    HOST_NAME2 = 'localhost2',
    PORT2 = 8081,
    SSL_FLAG2 = 1,
    PROXY_HOST = 'fakehost.com',
    PROXY_USER = 'proxyuser',
    PROXY_PASS = 'proxypass',
    PROXY_PORT = '1234';

describe('BrowserStackTunnel', function () {
  'use strict';

  beforeEach(function () {
    fsMock.fileNameModded = undefined;
    fsMock.mode = undefined;
    fsMock.fileNameCreated = undefined;
    fsMock.fileName = undefined;
    unzipMock.dirName = undefined;
    httpMock.url = undefined;
    osMock._platform = 'unknown';
    osMock._arch = 'unknown';
  });
    
  it('should error if stopped before started', function (done) {
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.stop(function (error) {
      expect(error.message).to.be('child not started');
      done();
    });
  });
  
  it('should error if no server listening on the specified host and port', function (done) {
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: INVALID_PORT,
        sslFlag: SSL_FLAG
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      expect(error.message).to.contain('Could not connect to server');
      done();
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  **Error: Could not connect to server: ----monkey');
  });

  it('should error if user provided an invalid key', function (done) {
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: 'MONKEY_KEY',
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      expect(error.message).to.contain('Invalid key');
      done();
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  **Error: You provided an invalid key ----monkey');
  });
  
  it('should error if started when already running', function (done) {
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      jarFile: JAR_FILE
    });

    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      }
      browserStackTunnel.start(function (error) {
        expect(error.message).to.be('child already started');
        done();
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  **Error: There is another JAR already running ----monkey');
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should download new jar if prompted that a new version exists as auto download is not compatible with our use of spawn', function (done) {
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: 'MONKEY_KEY',
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      }
      expect(fsMock.fileNameCreated).to.equal(JAR_FILE);
      expect(fsMock.fileName).to.equal(JAR_FILE);
      expect(httpMock.url).to.equal(JAR_URL);
      done();
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  **There is a new version of BrowserStackTunnel.jar available on server ----monkey');
    setTimeout(function () {
      process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
    }, 100);
  });

  it('should support multiple hosts', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }, {
        name: HOST_NAME2,
        port: PORT2,
        sslFlag: SSL_FLAG2
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG + ',' + HOST_NAME2 + ',' + PORT2 + ',' + SSL_FLAG2
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should use the specified jar file', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG,
        tunnelIdentifier: 'my_tunnel'
      }],
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should support the tunnelIdentifier option', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      tunnelIdentifier: 'my_tunnel',
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG,
            '-tunnelIdentifier',
            'my_tunnel'
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should support the skipCheck option', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      skipCheck: true,
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG,
            '-skipCheck'
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should support the v (verbose) option', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      v: true,
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG,
            '-v'
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should support the skipCheck option', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      skipCheck: true,
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG,
            '-skipCheck'
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  it('should support the proxy options', function (done) {
    spawnSpy.reset();
    var browserStackTunnel = new bs.BrowserStackTunnel({
      key: KEY,
      hosts: [{
        name: HOST_NAME,
        port: PORT,
        sslFlag: SSL_FLAG
      }],
      proxyUser: PROXY_USER,
      proxyPass: PROXY_PASS,
      proxyPort: PROXY_PORT,
      proxyHost: PROXY_HOST,
      jarFile: JAR_FILE
    });
    browserStackTunnel.start(function (error) {
      if (error) {
        expect().fail(function () { return error; });
      } else if (browserStackTunnel.state === 'started') {
        sinon.assert.calledOnce(spawnSpy);
        sinon.assert.calledWithExactly(
          spawnSpy,
          'java', [
            '-jar',
            JAR_FILE,
            KEY,
            HOST_NAME + ',' + PORT + ',' + SSL_FLAG,
            '-proxyHost',
            PROXY_HOST,
            '-proxyPort',
            PROXY_PORT,
            '-proxyUser',
            PROXY_USER,
            '-proxyPass',
            PROXY_PASS
          ]
        );
        done();
      }
    });

    process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
  });

  describe('on windows', function () {
    beforeEach(function () {
      osMock._platform = 'win32';
      osMock._arch = 'x64';
    });

    it('should download new binary if binary is not present', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        jarFile: NEW_JAR_FILE
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameCreated).to.equal(NEW_JAR_FILE);
        expect(fsMock.fileName).to.equal(NEW_JAR_FILE);
        expect(httpMock.url).to.equal(JAR_URL);
        done();
      });

      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should download new jar if prompted that a new version exists as auto download is not compatible with our use of spawn', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        jarFile: JAR_FILE
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameCreated).to.equal(JAR_FILE);
        expect(fsMock.fileName).to.equal(JAR_FILE);
        expect(httpMock.url).to.equal(JAR_URL);
        done();
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  **There is a new version of BrowserStackTunnel.jar available on server ----monkey');
      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should use the specified jar file', function (done) {
      spawnSpy.reset();
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: KEY,
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG,
          tunnelIdentifier: 'my_tunnel'
        }],
        jarFile: JAR_FILE
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        } else if (browserStackTunnel.state === 'started') {
          sinon.assert.calledOnce(spawnSpy);
          sinon.assert.calledWithExactly(
            spawnSpy,
            'java', [
              '-jar',
              JAR_FILE,
              KEY,
              HOST_NAME + ',' + PORT + ',' + SSL_FLAG
            ]
          );
          done();
        }
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
    });
  });

  describe('on osx', function () {
    beforeEach(function () {
      osMock._platform = 'darwin';
      osMock._arch = 'x64';
    });

    it('should download new binary if binary is not present', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        osxBin: NEW_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(NEW_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(NEW_BINARY_DIR);
        expect(httpMock.url).to.equal(OSX_BINARY_URL);
        done();
      });

      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should download new binary if prompted that a new version exists as auto download is not compatible with our use of spawn', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        osxBin: OSX_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(OSX_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(OSX_BINARY_DIR);
        expect(httpMock.url).to.equal(OSX_BINARY_URL);
        done();
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  **There is a new version of BrowserStackTunnel.jar available on server ----monkey');
      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should use the specified bin directory', function (done) {
      spawnSpy.reset();
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: KEY,
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG,
          tunnelIdentifier: 'my_tunnel'
        }],
        osxBin: OSX_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        } else if (browserStackTunnel.state === 'started') {
          sinon.assert.calledOnce(spawnSpy);
          sinon.assert.calledWithExactly(
            spawnSpy,
            OSX_BINARY_FILE, [
              KEY,
              HOST_NAME + ',' + PORT + ',' + SSL_FLAG
            ]
          );
          done();
        }
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
    });
  });

  describe('on linux x64', function () {
    beforeEach(function () {
      osMock._platform = 'linux';
      osMock._arch = 'x64';
    });
 
    it('should download new binary if binary is not present', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        linux64Bin: NEW_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(NEW_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(NEW_BINARY_DIR);
        expect(httpMock.url).to.equal(LINUX_64_BINARY_URL);
        done();
      });

      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should download new binary if prompted that a new version exists as auto download is not compatible with our use of spawn', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        linux64Bin: LINUX_64_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(LINUX_64_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(LINUX_64_BINARY_DIR);
        expect(httpMock.url).to.equal(LINUX_64_BINARY_URL);
        done();
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  **There is a new version of BrowserStackTunnel.jar available on server ----monkey');
      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should use the specified bin directory', function (done) {
      spawnSpy.reset();
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: KEY,
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG,
          tunnelIdentifier: 'my_tunnel'
        }],
        linux64Bin: LINUX_64_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        } else if (browserStackTunnel.state === 'started') {
          sinon.assert.calledOnce(spawnSpy);
          sinon.assert.calledWithExactly(
            spawnSpy,
            LINUX_64_BINARY_FILE, [
              KEY,
              HOST_NAME + ',' + PORT + ',' + SSL_FLAG
            ]
          );
          done();
        }
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
    });
  });

  describe('on linux ia32', function () {
    beforeEach(function () {
      osMock._platform = 'linux';
      osMock._arch = 'ia32';
    });

    it('should download new binary if binary is not present', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        linux32Bin: NEW_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(NEW_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(NEW_BINARY_DIR);
        expect(httpMock.url).to.equal(LINUX_32_BINARY_URL);
        done();
      });

      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should download new binary if prompted that a new version exists as auto download is not compatible with our use of spawn', function (done) {
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: 'MONKEY_KEY',
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG
        }],
        linux32Bin: LINUX_32_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        }
        expect(fsMock.fileNameModded).to.equal(LINUX_32_BINARY_FILE);
        expect(fsMock.mode).to.equal('0755');
        expect(unzipMock.dirName).to.equal(LINUX_32_BINARY_DIR);
        expect(httpMock.url).to.equal(LINUX_32_BINARY_URL);
        done();
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  **There is a new version of BrowserStackTunnel.jar available on server ----monkey');
      setTimeout(function () {
        process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
      }, 100);
    });

    it('should use the specified bin directory', function (done) {
      spawnSpy.reset();
      var browserStackTunnel = new bs.BrowserStackTunnel({
        key: KEY,
        hosts: [{
          name: HOST_NAME,
          port: PORT,
          sslFlag: SSL_FLAG,
          tunnelIdentifier: 'my_tunnel'
        }],
        linux32Bin: LINUX_32_BINARY_DIR
      });
      browserStackTunnel.start(function (error) {
        if (error) {
          expect().fail(function () { return error; });
        } else if (browserStackTunnel.state === 'started') {
          sinon.assert.calledOnce(spawnSpy);
          sinon.assert.calledWithExactly(
            spawnSpy,
            LINUX_32_BINARY_FILE, [
              KEY,
              HOST_NAME + ',' + PORT + ',' + SSL_FLAG
            ]
          );
          done();
        }
      });

      process.emit('mock:child_process:stdout:data', 'monkey-----  Press Ctrl-C to exit ----monkey');
    });
  });

  after(function () {
    childProcessMock.cleanUp();
  });
});