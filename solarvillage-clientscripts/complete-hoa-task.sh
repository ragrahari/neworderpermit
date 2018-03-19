#!/bin/bash

source ./env_variables.sh

if [ $# -ne 2 ]; then
    echo "desc   : Complete a Hoa Task and set whether the order is approved or not"
    echo "usage  : $0 <task_id> <isPermitApproved true|false>"
    echo "example: $0 1 true"
    exit 1
fi

TASK_ID=$1
DATA="'{\"approved\":\"$2\"}'"

URL=${KIE_SERVER_HOST}/kie-server/services/rest/server/containers/${KIE_CONTAINER_ID}/tasks/$TASK_ID/states/completed


echo "Complete task"
CURL="curl -X PUT  -H \"Accept: application/json\" -H \"Content-Type: application/json\"  -d ${DATA} --user ${CREDENTIALS} ${URL}"
if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi
echo ${CURL} | bash
