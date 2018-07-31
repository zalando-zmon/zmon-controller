const path = require('path');
const webpack = require('webpack');

const CONFIG = process.env.BUILD_CONFIG

var plugins = [];
var defines = {
    DEBUG: false
}
var bundleSuffix = (CONFIG === 'debug') ? '' : '.min';
var devtool = undefined;

switch(CONFIG) {
  case 'debug':
    defines.DEBUG = true;
    devtool = 'source-map';
    break;
  default:
    break;
}

var bundleName = 'opentracing-javascript-utils' + bundleSuffix;

module.exports = {
    entry: './src/lib.js',
    output: {
      filename: bundleName + '.js',
      path: path.resolve(__dirname, 'dist')
    },
    plugins: [
        new webpack.optimize.UglifyJsPlugin({
          include: /\.min\.js$/,
          minimize: true
        })
    ],
    module: {
        rules : [
            {
                test    : /\.js$/,
                exclude : /node_modules/,
                use: {
                  loader: 'babel-loader',
                  options: {
                      plugins : [
                          //
                          // Manually specify the *subset* of the ES2015 preset
                          // to use. This reduces the output file size and improves
                          // interoperability (e.g. Symbol polyfills on IE still
                          // don't work great).
                          //
                          'babel-plugin-transform-es2015-template-literals',
                          'babel-plugin-transform-es2015-literals',
                          //'babel-plugin-transform-es2015-function-name',
                          'babel-plugin-transform-es2015-arrow-functions',
                          'babel-plugin-transform-es2015-block-scoped-functions',
                          'babel-plugin-transform-es2015-classes',
                          'babel-plugin-transform-es2015-object-super',
                          // 'babel-plugin-transform-es2015-shorthand-properties',
                          'babel-plugin-transform-es2015-duplicate-keys',
                          'babel-plugin-transform-es2015-computed-properties',
                          // 'babel-plugin-transform-es2015-for-of',
                          'babel-plugin-transform-es2015-sticky-regex',
                          'babel-plugin-transform-es2015-unicode-regex',
                          'babel-plugin-check-es2015-constants',
                          'babel-plugin-transform-es2015-spread',
                          'babel-plugin-transform-es2015-parameters',
                          'babel-plugin-transform-es2015-destructuring',
                          'babel-plugin-transform-es2015-block-scoping',
                          //'babel-plugin-transform-es2015-typeof-symbol',
                          'babel-plugin-transform-es2015-modules-commonjs',
                        ]
                    }
                }
            }
        ]
    },
};
