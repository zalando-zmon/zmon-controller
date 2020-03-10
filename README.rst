ZMON source code on GitHub is no longer in active development. Zalando will no longer actively review issues or merge pull-requests.

ZMON is still being used at Zalando and serves us well for many purposes. We are now deeper into our observability journey and understand better that we need other telemetry sources and tools to elevate our understanding of the systems we operate. We support the `OpenTelemetry <https://opentelemetry.io>`_ initiative and recommended others starting their journey to begin there.

If members of the community are interested in continuing developing ZMON, consider forking it. Please review the licence before you do.

===============
ZMON Controller
===============

.. image:: https://travis-ci.org/zalando-zmon/zmon-controller.svg?branch=master
   :target: https://travis-ci.org/zalando-zmon/zmon-controller
   :alt: Build Status

.. image:: https://coveralls.io/repos/zalando-zmon/zmon-controller/badge.svg
   :target: https://coveralls.io/r/zalando-zmon/zmon-controller
   :alt: Coverage Status

.. image:: https://codecov.io/github/zalando-zmon/zmon-controller/coverage.svg?branch=master
   :target: https://codecov.io/github/zalando-zmon/zmon-controller?branch=master
   :alt: Codecov.io

.. image:: https://readthedocs.org/projects/zmon/badge/?version=latest
   :target: https://readthedocs.org/projects/zmon/?badge=latest
   :alt: Documentation Status

.. image:: https://slack.zmon.io/badge.svg
    :target: https://slack.zmon.io
    :alt: ZMON Slack Signup

.. image:: https://img.shields.io/badge/OpenTracing-enabled-blue.svg
    :target: http://opentracing.io
    :alt: OpenTracing enabled

ZMON Controller is the frontend UI and REST API for Zalando's open-source platform monitoring tool.

Running Locally
===============

Please use the docker-compose that is in the compose directory of the `main ZMON repository`_.

Make sure the provided docker-compose is up and all services are running.

.. code-block:: bash

    $ ./run-dev.sh

Now point your browser to https://localhost:8444/

To test service worker code locally in Chrome:

.. code-block:: bash

	/opt/google/chrome/chrome --unsafety-treat-insecure-origin-as-secure --ignore-certificate-errors



Running Unit and Database Tests
===============================

This will require Docker and automatically starts a local PostgreSQL database:

.. code-block:: bash

    $ ./test.sh

Running E2E Tests
====================

This will require npm

.. code-block:: bash

    $ ./e2e-test.sh

Building the Docker Image
=========================

.. code-block:: bash

    $ ./mvnw clean package
    $ docker build -t zmon-controller .


See also the `ZMON Documentation`_.

.. _main ZMON repository: https://github.com/zalando/zmon
.. _ZMON Documentation: https://docs.zmon.io/


Database API schema
===================

.. code-block:: bash

	find . -name '*sql' | sort -V | xargs cat > schema_os.sql

Iconsets
========

https://www.iconfinder.com/iconsets/CrystalClear by Everaldo Coelho
