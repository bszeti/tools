#!/bin/sh -x
# Send to an Azure Topic using SharedAccessSignature
# This code genreates the signature from key
# See (wrong hmac code example) https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-sas
# See (correct hmac code example) https://docs.microsoft.com/en-us/rest/api/eventhub/generate-sas-token

#urlencode
function urlencode() {
	echo -n "$1" | perl -MURI::Escape -ne 'print uri_escape($_)'
}

#Inputs
RESOURCE='bszeti.servicebus.windows.net/first'
KEY='+E948RpHAtIasfjsZdEM2utwmv9Flk7hkOFEhKIxk5s='
POLICY=mypolicy
TIMESTAMP=1548251906

HTTPURL="https://$RESOURCE/messages"
#url encoded resource is required for HMAC too
SBURLENCODED=$(urlencode "sb://$RESOURCE")


#The b64 encoded key is used, (no need to b64->byte->hex convert)
HMAC=$(echo -n -e "$SBURLENCODED\n$TIMESTAMP" | openssl sha256 -mac HMAC -macopt key:$KEY -binary | base64)
SAS="sr=$SBURLENCODED&sig=$(urlencode $HMAC)&se=$TIMESTAMP&skn=$POLICY"

echo $SAS
curl -vvv -X POST -H "Content-Type: application/xml" -H "Authorization: SharedAccessSignature $SAS" -d "<HELLO/>" $HTTPURL

#If policy has "Manage" for this topic, can get info about topic
# curl -s -H "Authorization: SharedAccessSignature $SAS" $HTTPURL/
# curl -s -H "Authorization: SharedAccessSignature $SAS" $HTTPURL/subscriptions
# curl -s -H "Authorization: SharedAccessSignature $SAS" $HTTPURL/subscriptions/mysubscription

#If policy has "Listen", can read messages
#Take and remove message:
# curl -s -H "Authorization: SharedAccessSignature $SAS" -X DELETE $HTTPURL/subscriptions/mysubscription/messages/head
#Lock, process and then remove message (by Location response header):
# curl -vvv -s -H "Authorization: SharedAccessSignature $SAS" -X POST -d "" $HTTPURL/subscriptions/mysubscription/messages/head
# curl -s -H "Authorization: SharedAccessSignature $SAS" -X DELETE $HTTPURL/subscriptions/mysubscription/messages/2/7da9cfd5-40d5-4bb1-8d64-ec5a52e1c547
#Unlock (put back)
# curl -s -H "Authorization: SharedAccessSignature $SAS" -X PUT $HTTPURL/subscriptions/mysubscription/messages/2/7da9cfd5-40d5-4bb1-8d64-ec5a52e1c547
