(function(w, d) {

  /**
   * List of supported Opentracing JS Libraries
   */
  const libraries = {
    lightstep: {
      class: 'lightstep',
      src: '',        // TODO add CDN url
    }
  };

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
          this.dependencies.push(lib.src);
      }

      return this.dependencies.map(this.getScript)
    }

    /**
     * Initializes Global Tracer with Custom library
     */
    initGlobalTracer(lib) {
      if (lib && w[lib.class]) {
        console.log('using lib')
        opentracing.initGlobalTracer(new w[lib.class].Tracer(this.config));
      } else {
        console.log('no-op')
        opentracing.initGlobalTracer();
      }
    }

    getTracerPromise(lib, resolve, reject) {
      return new Promise((resolve, reject) => {
        this.initGlobalTracer(lib)
        resolve(opentracing.globalTracer())
      });
    }


    /**
     * Load dependencies and initialize Tracer
     */
    initOpenTracing({ name = 'opentracing', config = {}}, resolve, reject) {

      let lib = libraries[name];
      this.config = config;

      if (name !== 'opentracing' && !lib) {
        let m = `Unkown Tracing Library: ${name}. Currently supported: ${Object.keys(libraries)}`;
        console.log(m);
      }

      return new Promise((resolve, reject) => {
        Promise
        .all(this.getDependencies(lib))
        .then( () => {
          this.initGlobalTracer(lib)
          resolve(opentracing.globalTracer())
        })
        .catch( (e) => {
          console.log("Can't load Tracing library, defaulting to no-op")
          this.initGlobalTracer()
          resolve(opentracing.globalTracer())
        })
      })

    }

  }

  w.opentracingJavascriptUtils = new Tracer();

})(window, document);
