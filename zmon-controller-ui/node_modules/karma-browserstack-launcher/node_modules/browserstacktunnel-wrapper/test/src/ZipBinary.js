var expect = require('expect.js');
var path = require('path');

var mocks = require('mocks'),
    httpMock = require('../lib/mocks').httpMock,
    fsMock = require('../lib/mocks').fsMock,
    unzipMock = require('../lib/mocks').unzipMock;

var zb = mocks.loadFile('./src/ZipBinary.js', {
  https: httpMock,
  fs: fsMock,
  unzip: unzipMock
});
var ZipBinary = zb.ZipBinary;

var PLATFORM = 'platform';
var ARCH = 'arch';
var DEFAULT_BINARY_DIR = path.resolve(path.join(__dirname, '../../bin', PLATFORM, ARCH));
var DEFAULT_BINARY_FILE = path.join(DEFAULT_BINARY_DIR, 'BrowserStackLocal');
var OTHER_BINARY_DIR = '/bin';
var OTHER_BINARY_FILE = path.join(OTHER_BINARY_DIR, 'BrowserStackLocal');
var ZIP_URL = 'https://www.browserstack.com/browserstack-local/BrowserStackLocal-' + PLATFORM + '-' + ARCH + '.zip';

describe('ZipBinary', function () {
  'use strict';

  var zipBinary;

  beforeEach(function () {
    fsMock.fileNameModded = undefined;
    fsMock.mode = undefined;
    unzipMock.dirName = undefined;
    httpMock.url = undefined;
  });

  describe('with default binary path', function () {
    beforeEach(function () {
      zipBinary = new ZipBinary(PLATFORM, ARCH);
    });

    it('should have the correct path', function () {
      expect(zipBinary.path).to.equal(DEFAULT_BINARY_FILE);
    });

    it('should have the correct command', function () {
      expect(zipBinary.command).to.equal(DEFAULT_BINARY_FILE);
    });

    it('should have the correct args', function () {
      expect(zipBinary.args).to.eql([]);
    });

    describe('#update', function () {
      it('should download the jar file', function (done) {
        zipBinary.update(function () {
          expect(fsMock.fileNameModded).to.equal(DEFAULT_BINARY_FILE);
          expect(fsMock.mode).to.equal('0755');
          expect(unzipMock.dirName).to.equal(DEFAULT_BINARY_DIR);
          expect(httpMock.url).to.equal(ZIP_URL);
          done();
        });
      });
    });
  });

  describe('with given binary path', function () {
    beforeEach(function () {
      zipBinary = new ZipBinary(PLATFORM, ARCH, OTHER_BINARY_DIR);
    });

    it('should have the correct path', function () {
      expect(zipBinary.path).to.equal(OTHER_BINARY_FILE);
    });

    it('should have the correct command', function () {
      expect(zipBinary.command).to.equal(OTHER_BINARY_FILE);
    });

    it('should have the correct args', function () {
      expect(zipBinary.args).to.eql([]);
    });

    describe('#update', function () {
      it('should download the jar file', function (done) {
        zipBinary.update(function () {
          expect(fsMock.fileNameModded).to.equal(OTHER_BINARY_FILE);
          expect(fsMock.mode).to.equal('0755');
          expect(unzipMock.dirName).to.equal(OTHER_BINARY_DIR);
          expect(httpMock.url).to.equal(ZIP_URL);
          done();
        });
      });
    });
  });
});
