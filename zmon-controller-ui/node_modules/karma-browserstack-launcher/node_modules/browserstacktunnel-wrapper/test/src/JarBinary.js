var expect = require('expect.js');
var path = require('path');

var mocks = require('mocks'),
    httpMock = require('../lib/mocks').httpMock,
    fsMock = require('../lib/mocks').fsMock;

var jb = mocks.loadFile('./src/JarBinary.js', {
  http: httpMock,
  'fs-extra': fsMock
});
var JarBinary = jb.JarBinary;

var DEFAULT_JAR_FILE = path.resolve(path.join(__dirname, '../../bin/jar/BrowserStackTunnel.jar'));
var OTHER_JAR_FILE = '/bin/tunnel.jar';
var JAR_URL = 'http://www.browserstack.com/BrowserStackTunnel.jar';

describe('JarBinary', function () {
  'use strict';

  var jarBinary;

  beforeEach(function () {
    fsMock.fileName = undefined;
    httpMock.url = undefined;
  });

  describe('with default binary path', function () {
    beforeEach(function () {
      jarBinary = new JarBinary();
    });

    it('should have the correct path', function () {
      expect(jarBinary.path).to.equal(DEFAULT_JAR_FILE);
    });

    it('should have the correct command', function () {
      expect(jarBinary.command).to.equal('java');
    });

    it('should have the correct args', function () {
      expect(jarBinary.args).to.eql(['-jar', DEFAULT_JAR_FILE]);
    });

    describe('#update', function () {
      it('should download the jar file', function (done) {
        jarBinary.update(function () {
          expect(fsMock.fileNameCreated).to.equal(DEFAULT_JAR_FILE);
          expect(fsMock.fileName).to.equal(DEFAULT_JAR_FILE);
          expect(httpMock.url).to.equal(JAR_URL);
          done();
        });
      });
    });
  });

  describe('with given binary path', function () {
    beforeEach(function () {
      jarBinary = new JarBinary(OTHER_JAR_FILE);
    });

    it('should have the correct path', function () {
      expect(jarBinary.path).to.equal(OTHER_JAR_FILE);
    });

    it('should have the correct command', function () {
      expect(jarBinary.command).to.equal('java');
    });

    it('should have the correct args', function () {
      expect(jarBinary.args).to.eql(['-jar', OTHER_JAR_FILE]);
    });

    describe('#update', function () {
      it('should download the jar file', function (done) {
        jarBinary.update(function () {
          expect(fsMock.fileNameCreated).to.equal(OTHER_JAR_FILE);
          expect(fsMock.fileName).to.equal(OTHER_JAR_FILE);
          expect(httpMock.url).to.equal(JAR_URL);
          done();
        });
      });
    });
  });
});
