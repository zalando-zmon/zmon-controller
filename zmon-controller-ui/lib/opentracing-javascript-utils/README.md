# OpenTracing JavaScript Utils

Utilities to add OpenTracing EUMs

### Early stage

## Features
* Dynamically loads dependencies depending on chosen Tracing library
* Promises to return an OpenTracing globalTracer
* Defaults to no-op
* Currently supports: lightstep-tracer.

## Example

```html
<script src="opentracing-javascript-utils.js"></script>
<script type="text/javascript">

  opentracingJavascriptUtils.initOpenTracing({
    name: 'lightstep',
    config: {
      xhr_instrumentation: true,
      access_token: '1234',
      component_name: 'example',
      collector_host: 'localhost',
      collector_port: 443,
      collector_encryption: '',
      verbosity: 1,
    }})
    .then((globalTracer) => {
      // ...
    })

</script>
```
