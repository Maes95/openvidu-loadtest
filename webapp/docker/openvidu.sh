#!/bin/bash
export OPENVIDU_URL=$(hostname -i);
java -jar -Dspring.profiles.active=docker -Dopenvidu.publicurl="https://${OPENVIDU_URL}:4443/" /openvidu-server.jar;