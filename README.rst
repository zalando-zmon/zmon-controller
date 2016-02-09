===============
ZMON Controller
===============

.. image:: https://travis-ci.org/zalando/zmon-controller.svg?branch=master
   :target: https://travis-ci.org/zalando/zmon-controller
   :alt: Build Status

.. image:: https://coveralls.io/repos/zalando/zmon-controller/badge.svg
   :target: https://coveralls.io/r/zalando/zmon-controller
   :alt: Coverage Status

.. image:: https://readthedocs.org/projects/zmon/badge/?version=latest
   :target: https://readthedocs.org/projects/zmon/?badge=latest
   :alt: Documentation Status
   
ZMON Controller is the frontend UI and REST API for Zalando's open-source platform monitoring tool.

Running Locally
===============

Please use the `main ZMON repository`_ to start a Vagrant demo box.

Make sure that port forwarding for TCP port 8443 is commented out in your ``Vagrantfile``.

Make sure the provided Vagrant-Box is up and all services are running.

.. code-block:: bash

    $ ./mvnw clean install
    $ export SPRING_PROFILES_ACTIVE=github     # use GitHub auth
    $ export ZMON_OAUTH2_SSO_CLIENT_ID=344c9a90fc697fe6662a
    $ export ZMON_OAUTH2_SSO_CLIENT_SECRET=a2bbb03a29f6737af04c77f2d88e8f8199ff179b
    $ export ZMON_AUTHORITIES_SIMPLE_ADMINS=*  # everybody is admin!
    $ export REDIS_PORT=38086                  # use Redis in Vagrant box
    $ export POSTGRES_URL=jdbc:postgresql://localhost:38088/local_zmon_db
    $ java -jar zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar

Now point your browser to https://localhost:8443/

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
.. _ZMON Documentation: https://zmon.readthedocs.org/
