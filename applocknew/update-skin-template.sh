#!/bin/bash
dirs='layout values values-v13 values-sw240dp values-sw480dp values-sw600dp'
template='/d/Workspace/android/skin-template'

for dir in $dirs
do
    for xml in `ls $template/res/$dir`
    do
        echo copying $xml
        cp -f src/main/res/$dir/$xml $template/res/$dir/$xml
    done
done

cd build/intermediates/classes/debug
if [ $? = 1 ]; then
    cd build/intermediates/classes/release
    if [ $? = 1 ]; then
        echo "error, can't find classes that exists under folder build/intermediates/classes"
        read
        exit
    fi
fi

cat > MANIFEST.MF <<manifest
Manifest-Version: 1.0 
Created-By: Abc Company
manifest
jar cvfm bridge.jar MANIFEST.MF *.* 

if [ $? = 1 ]; then
    echo "error, can't build a bridge.jar for skin"
    read
    exit
fi

cp -f bridge.jar $template/libs/bridge.jar

cd $template
git add --all res libs
git commit -m "auto update from applock newest version"
git push serverbak master

echo 'login to server 5'

ssh root@10.80.10.5
