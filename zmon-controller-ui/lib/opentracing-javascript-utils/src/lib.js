var opentracing = require('opentracing');
var {libraries} = require('./libraries');

(function component(w, d) {
  console.log('libraries', libraries)

  /**
   * get current working directory to load getDependencies
   */
  let currentPath = () => {
    let scripts = Array.from(d.getElementsByTagName("script"));
    let ix = scripts.findIndex(
      (s) => s.src.indexOf('opentracing-javascript-utils.js') >= 0
    )
    let path = scripts[ix].src.split('opentracing-javascript-utils.js')[0];
    return path
  }

  /**
   * OpenTracing Tracer loads dependencies and initializes a global
   * tracer with specified library implementation; defaults to no-op.
   */
  class Tracer {

    constructor() {
      this.dependencies = [];
    }

    /**
     * Load external script and return a Promise
     * to be resolved when script is loaded
     */
    getScript(url) {

      let head = d.documentElement;
      let script = null;

      return new Promise(function(resolve, reject) {

        let s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = url;
        s.async = true;

        s.onload = function() {
          resolve(url);
        }

        s.onerror = function(e, x) {
          reject(url);
        }

        d.documentElement.appendChild(s);
      });
    };

    /**
     * List of dependencies to load sync
     */
    getDependencies(lib) {
      if (lib && !w[lib.class]) {
        if (lib.src && Array.isArray(lib.src)) {
          lib.src.forEach((s) => this.dependencies.push(s))
        } else {
          this.dependencies.push(lib.src);
        }
      }
      let d = this.dependencies.map(this.getScript)
      return d
    }


    /**
     * Load dependencies and initialize Tracer
     */
    initOpenTracing(tracerConfig, resolve, reject) {

      let lib = libraries[tracerConfig.name] || libraries['opentracing']

      if (!libraries[tracerConfig.name]) {
        let m = `Unkown Tracing Library: "${tracerConfig.name}". Currently supported: ${Object.keys(libraries)}`;
        console.log(m);
      }

      return new Promise((resolve, reject) => {
        Promise
        .all(this.getDependencies(lib))
        .then( () => {
          let customTracer = lib.getCustomTracer(tracerConfig)
          opentracing.initGlobalTracer(customTracer)
          resolve(opentracing.globalTracer())
        })
        .catch( (e) => {
          console.log("Can't load Tracing library, defaulting to no-op.", e)
          opentracing.initGlobalTracer()
          resolve(opentracing.globalTracer())
        })
      })

    }

  }

  return w.opentracingJavascriptUtils = new Tracer();
})(window, document)
