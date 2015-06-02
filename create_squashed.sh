
export IMAGE=zmon-controller
docker build -t os-registry.stups.zalan.do/stups/$IMAGE:$1-unsquashed .
docker save os-registry.stups.zalan.do/stups/$IMAGE:$1-unsquashed | docker-squash -verbose -t os-registry.stups.zalan.do/stups/$IMAGE:$1 | docker load
docker push os-registry.stups.zalan.do/stups/$IMAGE:$1