#!/bin/bash

source ./env_variables.sh


if [ $# -ne 1 ]; then
    echo "desc   : List of tasks for potential and/or current owners"
    echo "usage  : $0 <task-owner-groups>"
    echo "example: $0 sales"
    exit 1
fi

GROUPS=$1

echo "Potential owners"
URL=${KIE_SERVER_HOST}/kie-server/services/rest/server/queries/tasks/instances/pot-owners?groups=$1
CURL="curl -X GET -H \"Accept: application/json\" --user ${CREDENTIALS} ${URL}"
if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi
echo ${CURL} | bash


echo "Owners"
URL=${KIE_SERVER_HOST}/kie-server/services/rest/server/queries/tasks/instances/owners
CURL="curl -X GET -H \"Accept: application/json\" --user ${CREDENTIALS} ${URL}"
if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi
echo ${CURL} | bash
