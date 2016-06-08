#!/bin/bash

UIDIR='./zmon-controller-ui'

cd $UIDIR

echo 'Installing necessary npm modules..'
npm install

echo 'Running UI Unit tests..'
npm test
