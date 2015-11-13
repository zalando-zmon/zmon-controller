#!/bin/bash
# Helper script to ease frontend development
# symlinks some folders which are pulled into the final WAR by the the maven-war-plugin
# so the developer can use tomcat7:run and directly edit the JS/CSS files without having to restart the server
mvn clean
for folder in lib asset; do
    p="target/war/work/de.zalando/angularjs-seed/$folder"
    if [ ! -d "$p" ]; then
        mvn clean package -Denforcer.skip=true -Dmaven.test.skip
    fi
    if [ ! -d src/main/webapp/$folder ]; then
        mkdir src/main/webapp/$folder
    fi
    for i in $p/*; do
        if [ -d $i ];then
            dir=`basename $i`
            abs=$(cd $i && pwd)
            ln -sf $abs src/main/webapp/$folder/$dir
        fi
    done
done
echo 'You should now be able to use "mvn tomcat7:run"'
