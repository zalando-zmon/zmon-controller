export IMAGE=zmon-controller

docker build -t registry.opensource.zalan.do/stups/$IMAGE:$1-unsquashed .
docker save registry.opensource.zalan.do/stups/$IMAGE:$1-unsquashed | docker-squash -verbose -t registry.opensource.zalan.do/stups/$IMAGE:$1 | docker load
docker push registry.opensource.zalan.do/stups/$IMAGE:$1
