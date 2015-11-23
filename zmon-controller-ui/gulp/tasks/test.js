'use strict';

var pkg = require('../../package.json');
var logger = require('../logger');
var gulp = require('gulp');
var config = pkg.gulpConfiguration.test;
var spawn = require('child_process').spawn;

// gulp test --env local --type acceptance --tests ./tests/acceptance/test.spec.js
var task = function(cb) {

    var args = process.argv
                .map(function(arg) { return arg.replace('--tests', '--specs') })
                .filter(function(arg, index) {return index > 2;});

    var type = ~args.indexOf('--type') ? args[args.indexOf('--type') + 1] : 'acceptance';
    var env = ~args.indexOf('--env') ? args[args.indexOf('--env') + 1] : 'local';

    var commands = {
        acceptance :'./node_modules/protractor/bin/protractor',
        unit: './node_modules/karma/bin/karma'
    };

    args =  (
        type === 'unit' ? ['start', config[type][env].config] : [config[type][env].config]
    ).concat(args);

    var proc = spawn(commands[type], args);

    var error = '';
    proc.stderr.on('data', function(err) { error += err.toString().trim()});
    proc.stdout.on('data', function(data) { process.stdout.write(data.toString() + '\b')});
    proc.on('close', function(code) { cb(error || null); });
};

module.exports = {
    run: task,
    deps: config.deps || []
}
