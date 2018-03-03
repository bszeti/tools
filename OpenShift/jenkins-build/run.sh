#!/bin/sh
oc create -f ./oc-resources.yaml
oc create configmap maven-settings --from-file=settings.xml=settings.xml --from-file=myTruststore.jks=myTruststore.jks
