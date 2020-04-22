# CREARE NETWORK
docker network create elastest_elastest

# DELETE ALL CONTAINERS
docker rm -f $(docker ps -aq)

# RUN SUT
docker-compose up -d

EUS=$(docker inspect --format='{{ .NetworkSettings.Networks.elastest_elastest.IPAddress}}' openviduloadtest_eus_1)
export ET_EUS_API=http://$EUS:8040/eus/v1/
export ET_SUT_HOST=$(docker inspect --format='{{ .NetworkSettings.Networks.elastest_elastest.IPAddress}}' openviduloadtest_sut_1)

#cd selenium-test/
#mvn -Dtest=OpenViduLoadTest -DAPP_URL="http://$ET_SUT_HOST:8080/" -DOPENVIDU_URL="https://$ET_SUT_HOST:4443/" -DRESULTS_PATH=../../results/ -DSESSIONS=1 -DUSERS_SESSION=2 -DPRIVATE_KEY_PATH=../../openvidu-loadtest/webapp/key.pem test