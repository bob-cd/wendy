#!/bin/env bash


# This file is part of Wendy.
#
# Wendy is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Wendy is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Wendy. If not, see <http://www.gnu.org/licenses/>.


# This script builds wendy on bob.
# Dependencies: bash, curl, tar, jq
# Premises: - Running a Bob instance on localhost:7777
#           - Running an artifact store on localhost:8001
#           - Running an external resource on localhost:8000
# Manual: - Start Docker daemon e.g. with systemctl start docker
#         - Start Bob's database e.g. with 
#             docker run --detach --name bob-db -p 5432:5432 -e POSTGRES_DB=bob -e POSTGRES_USER=bob postgres:alpine
#         - Start Bob's artifact store e.g. with
#             docker run --detach --name bob-artifact -p 8001:8001 bobcd/artifact-local
#         - Start Bob's external resource e.g. with
#             docker run --detach --name bob-resource -p 8000:8000 bobcd/resource-git
#         - Start Bob e.g. with
#             docker run --detach --privileged --name bob -p 7777:7777 bobcd/bob

set -o errexit    # Used to exit upon error, avoiding cascading errors
set -o pipefail   # Unveils hidden failures
set -o nounset    # Exposes unset variables

TIMEOUT=300
HOST=localhost
PORT=7777

# registering an artifact store with bob
curl -X POST \
     --header 'Content-Type: application/json' \
     --header 'Accept: application/json' \
     -d "{\"url\": \"http://${HOST}:8001\"}" \
     "http://${HOST}:${PORT}/api/artifact-stores/local"
# registering an external resource with bob
curl -X POST \
    --header 'Content-Type: application/json' \
    --header 'Accept: application/json' \
    -d "{\"url\": \"http://${HOST}:8000\"}" \
    "http://${HOST}:7777/api/external-resources/git"
# delete possibly existing pipeline
curl -X DELETE \
     --header 'Accept: application/json' \
     "http://${HOST}:${PORT}/api/pipelines/groups/wendy/names/build"
# create pipeline wendy/build
curl -X POST \
     --header 'Content-Type: application/json' \
     --header 'Accept: application/json' \
     --data '{"image":"oracle/graalvm-ce:19.3.0","resources":[{"provider":"git","name":"my-source","type":"external","params":{"repo":"https://github.com/bob-cd/wendy.git","branch":"master"}}],"steps":[{"cmd":"gu install native-image"},{"cmd":"yum install -y wget"},{"cmd":"wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein"},{"cmd":"chmod +x /usr/local/bin/lein"},{"needs_resource":"my-source","cmd":"/usr/local/bin/lein native-image","produces_artifact":{"path":"target/default+uberjar/wendy","name":"wendy","store":"local"}}]}' \
     "http://${HOST}:${PORT}/api/pipelines/groups/wendy/names/build"
# start pipeline
curl -X POST \
     --header 'Content-Type: application/json' \
     --header 'Accept: application/json' \
     "http://${HOST}:${PORT}/api/pipelines/start/groups/wendy/names/build"
# query execution until build passed or timeouted
while [[ $(curl --silent \
                -X GET \
                --header 'Accept: application/json' \
                "http://${HOST}:${PORT}/api/pipelines/status/groups/wendy/names/build/number/1" | \
                jq '.message == "passed"') \
                == "false" ]]; do 
    if [[ $TIMEOUT == 0 ]]; then echo "Aborted due to timeout" && exit 1; fi
    TIMEOUT=$((TIMEOUT - 3))
    echo "Timeout in: ${TIMEOUT}"
    sleep 3
done
# download and unarchive wendy
curl --silent -X GET "http://${HOST}:8001/bob_artifact/wendy/build/1/wendy" | tar xv
# test wendy
./wendy can-we-build-it
