#!/bin/bash

#mvn -Dmaven.repo.local=./localrepo2/ -s settings.xml -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true $@
PATH=apache-maven-2.2.1/bin:$PATH mvn -s m2settings.xml -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true $@
