///<reference path="../../headers/common.d.ts" />
System.register([], function(exports_1) {
    var waitSeconds, head, links, linkHrefs, i, isWebkit, webkitLoadCheck, noop, loadCSS;
    function fetch(load) {
        if (typeof window === 'undefined') {
            return '';
        }
        // dont reload styles loaded in the head
        for (var i = 0; i < linkHrefs.length; i++) {
            if (load.address === linkHrefs[i]) {
                return '';
            }
        }
        return loadCSS(load.address);
    }
    exports_1("fetch", fetch);
    return {
        setters:[],
        execute: function() {
            waitSeconds = 100;
            head = document.getElementsByTagName('head')[0];
            // get all link tags in the page
            links = document.getElementsByTagName('link');
            linkHrefs = [];
            for (i = 0; i < links.length; i++) {
                linkHrefs.push(links[i].href);
            }
            isWebkit = !!window.navigator.userAgent.match(/AppleWebKit\/([^ ;]*)/);
            webkitLoadCheck = function (link, callback) {
                setTimeout(function () {
                    for (var i = 0; i < document.styleSheets.length; i++) {
                        var sheet = document.styleSheets[i];
                        if (sheet.href === link.href) {
                            return callback();
                        }
                    }
                    webkitLoadCheck(link, callback);
                }, 10);
            };
            noop = function () { };
            loadCSS = function (url) {
                return new Promise(function (resolve, reject) {
                    var link = document.createElement('link');
                    var timeout = setTimeout(function () {
                        reject('Unable to load CSS');
                    }, waitSeconds * 1000);
                    var _callback = function (error) {
                        clearTimeout(timeout);
                        link.onload = link.onerror = noop;
                        setTimeout(function () {
                            if (error) {
                                reject(error);
                            }
                            else {
                                resolve('');
                            }
                        }, 7);
                    };
                    link.type = 'text/css';
                    link.rel = 'stylesheet';
                    link.href = url;
                    if (!isWebkit) {
                        link.onload = function () { _callback(undefined); };
                    }
                    else {
                        webkitLoadCheck(link, _callback);
                    }
                    link.onerror = function (evt) {
                        _callback(evt.error || new Error('Error loading CSS file.'));
                    };
                    head.appendChild(link);
                });
            };
        }
    }
});
//# sourceMappingURL=css_loader.js.map