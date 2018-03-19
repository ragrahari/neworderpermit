#!/bin/bash

source ./env_variables.sh

echo "Intelligent server ${KIE_SERVER_HOST}"
curl -X GET -H "Accept: application/json" --user ${CREDENTIALS} ${KIE_SERVER_HOST}/kie-server/services/rest/server


echo "Containers"
curl -X GET -H "Accept: application/json" --user ${CREDENTIALS} ${KIE_SERVER_HOST}/kie-server/services/rest/server/containers


echo "${KIE_CONTAINER_ID}"
curl -X GET -H "Accept: application/json" --user ${CREDENTIALS} ${KIE_SERVER_HOST}/kie-server/services/rest/server/containers/${KIE_CONTAINER_ID}

echo "Process Definitions"
curl -X GET -H "Accept: application/json" --user ${CREDENTIALS} ${KIE_SERVER_HOST}/kie-server/services/rest/server/queries/containers/${KIE_CONTAINER_ID}/processes/definitions
