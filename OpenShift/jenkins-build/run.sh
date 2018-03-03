#!/bin/sh
oc create -f ./git-ssh.template
oc create -f ./git-secret.template
oc create -f ./git-config.template
oc create -f ./maven-secret.template
oc create configmap maven-settings --from-file=settings.xml=settings.xml --from-file=myTruststore.jks=myTruststore.jks
