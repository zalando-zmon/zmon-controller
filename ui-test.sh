#!/bin/bash

echo 'Make sure the provided Vagrant-Box is up and all services are running.'

UIDIR='./zmon-controller-ui'

cd $UIDIR

echo 'Installing necessary npm modules..'
npm install

echo 'Running UI Unit tests..'
npm test
