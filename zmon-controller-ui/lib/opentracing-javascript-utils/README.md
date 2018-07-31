# OpenTracing JavaScript Utils

Utilities to Instrument JavaScript Web Applications with OpenTracing.

### Early stage

## Features
* Dynamically loads dependencies depending on chosen Tracing library
* Promises to return an OpenTracing globalTracer
* Defaults to no-op
* Currently supports: lightstep.

## Example

```html
<script src="opentracing-javascript-utils.min.js"></script>
<script type="text/javascript">

  opentracingJavascriptUtils.initOpenTracing({
    name: 'lightstep',
    config: {
      xhr_instrumentation: true,
      access_token: '123456',
      component_name: 'myApp',
      collector_host: 'localhost',
      collector_port: 443,
      collector_encryption: 'tls',
      verbosity: 1,
    }})
    .then((globalTracer) => {
      // bootstrap myApp
    })

</script>
```
