(function(w, d) {

  /**
   * List of supported Opentracing JS Libraries
   */
  const libraries = {
    lightstep: {
      class: 'lightstep',
      src: 'lightstep-tracer.js',
      deps: [ 'opentracing-browser.min.js' ],
    },
    opentracing: {
      src: 'opentracing-browser.min.js',
    }
  };

  /**
   * get current working directory to load getDependencies
   * FIXME ugly as hell but works for now...
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
        s.src = `${currentPath()}lib/${url}`;
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
      if (lib) {
        this.dependencies = this.dependencies.concat(lib.deps || []);
        this.dependencies.push(lib.src);
      }

      return this.dependencies.map(this.getScript)
    }

    /**
     * Initializes Global Tracer with Custom library
     */
    initGlobalTracer(lib) {
      if (lib && lib.class && w[lib.class]) {
        opentracing.initGlobalTracer(new w[lib.class].Tracer(this.config));
      }
    }

    /**
     * Load dependencies and initialize Tracer
     */
    initOpenTracing({name, config} = { name: 'opentracing' }, resolve, reject) {
      this.config = config;
      let lib = libraries[name];
      return new Promise((resolve, reject) => {
        Promise
        .all(this.getDependencies(lib))
        .then( () => {
          this.initGlobalTracer(lib)
          resolve(opentracing.globalTracer())
        })
        .catch( (e) => {
          reject(e)
        })
      })
    }

  }

  w.opentracingJavascriptUtils = new Tracer();

})(window, document);
