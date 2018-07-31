export const libraries = {
  opentracing: {
  	class: 'opentracing',

  	src: [],

  	getCustomTracer: () => {}
  },
  lightstep: {
    
    class: 'lightstep',
    
    src: [
    	'https://rawgit.com/lightstep/lightstep-tracer-javascript/v0.20.3/dist/lightstep-tracer.min.js',
		'https://rawgit.com/lightstep/lightstep-overlay/v1.1.4/dist/lightstep-overlay.min.js'
	],

	getCustomTracer: (config) => {
    	let customTracer = new lightstep.Tracer(config.config);

    	if (config.overlay) {
    		LightStepOverlay(customTracer);
    		console.log('Lightstep Overlay enabled')
    	}

    	return customTracer
    }
  }
}
