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

ZMON Controller is the frontend UI and REST API for Zalando's open-source platform monitoring tool.

Running Locally
===============

Please use the `main ZMON repository`_ to start a Vagrant demo box.

Make sure the provided Vagrant-Box is up and all services are running.

.. code-block:: bash

    $ ./run-dev.sh

Now point your browser to https://localhost:8444/

Running Unit and Database Tests
===============================

This will require Docker and automatically starts a local PostgreSQL database:

.. code-block:: bash

    $ ./test.sh


Building the Docker Image
=========================

.. code-block:: bash

    $ sudo pip3 install scm-source
    $ scm-source -f target/scm-source.json
    $ docker build -t zmon-controller .


See also the `ZMON Documentation`_.

.. _main ZMON repository: https://github.com/zalando/zmon
.. _ZMON Documentation: https://docs.zmon.io/


Database API schema
===================

.. code-block:: bash

	find . -name '*sql' | sort -V | xargs cat > schema_os.sql

