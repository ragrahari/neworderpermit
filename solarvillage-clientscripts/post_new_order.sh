#!/bin/sh

source ./env_variables.sh

if [ $# -lt 3 ]; then
    echo "desc   :  Create and start a new order permit process instance"
    echo "usage  : $0 <address> <id> <isHOAMember true|false> <hoaMeetingDate (MM/DD/YYYY)>"
    echo "example1: $0 \"4201 Spring Valley Rd\" \"15500\" \"true\" \"05/05/2018\""
    echo "example2: $0 \"134 Irvine Rd\" \"15500\" \"false\""
    exit 1
fi


CONTAINER=${KIE_SERVER_HOST}/kie-server/services/rest/server/containers/${KIE_CONTAINER_ID}
URL=${CONTAINER}/processes/SolarVillageProj.NewOrder/instances
DATA="'{\"address\":\"$1\", \"id\":\"$2\", \"isHOAMember\":\"$3\", \"hoaMeetingDate\":\"$4\"}'"

echo ${DATA}
echo ${URL}

CURL="curl -X POST -H \"Accept: application/json\" -H \"Content-Type: application/json\" --user ${CREDENTIALS} -d ${DATA} ${URL}"

if [ ${PRINT_CURL_COMMAND} == 'true' ]; then
  echo $CURL
fi

echo ${CURL} | bash
