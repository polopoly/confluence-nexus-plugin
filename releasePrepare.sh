#!/bin/bash

SETTINGS=$1
shift
PATH=apache-maven-2.2.1/bin:$PATH mvn -s ${SETTINGS} \
  -Dmaven.wagon.http.ssl.insecure=true \
  -Dmaven.wagon.http.ssl.allowall=true \
  -Darguments='-DskipTests' \
  -DskipTests \
  clean \
  install \
  deploy \
  release:prepare \
  -Pdeploy \
  -DautoVersionSubmodules=true \
  $@
