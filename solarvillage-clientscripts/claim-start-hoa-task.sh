#!/bin/bash

source ./env_variables.sh

if [ $# -ne 1 ]; then
    echo "desc   : Claim and start a HOA User task"
    echo "usage  : $0 <task-id>"
    echo "example: $0 1"
    exit 1
fi

TASK_ID=$1

URL=${KIE_SERVER_HOST}/kie-server/services/rest/server/containers/${KIE_CONTAINER_ID}/tasks/$TASK_ID/states/claimed
echo "Claim task"
CURL="curl -X PUT -H \"Accept: application/json\" --user ${CREDENTIALS} ${URL}"
if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi
echo ${CURL} | bash

URL=${KIE_SERVER_HOST}/kie-server/services/rest/server/containers/${KIE_CONTAINER_ID}/tasks/$TASK_ID/states/started
echo "Start task"
CURL="curl -X PUT -H \"Accept: application/json\" --user ${CREDENTIALS} ${URL}"

if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi

echo ${CURL} | bash
