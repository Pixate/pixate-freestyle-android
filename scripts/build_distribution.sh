#!/bin/sh

 . ${PX_FREESTYLE_SCRIPT:-$(dirname $0)}/common.sh


VERSION=$PX_FREESTYLE_VERSION
VERSION="0.3"

echo "Building Pixate Freestyle Distribution v${VERSION}"

cd $PX_FREESTYLE_ROOT

git archive --format=zip --prefix=pixate-android-freestyle-$VERSION/ -9  -o $PX_FREESTYLE_ROOT/build/pixate-android-freestyle-$VERSION.zip HEAD
 
mkdir -p $PX_FREESTYLE_ROOT/build

zip $PX_FREESTYLE_ROOT/build/pixate-android-freestyle-$VERSION.zip  -d \*/.idea/\*

