#!/bin/bash

source ./env_variables.sh

echo "List of all process-instances:"
curl -X GET -H "Accept: application/json" --user ${CREDENTIALS} ${KIE_SERVER_HOST}/kie-server/services/rest/server/queries/containers/${KIE_CONTAINER_ID}/process/instances
