#!/bin/sh

 . ${PX_FREESTYLE_SCRIPT:-$(dirname $0)}/common.sh


VERSION=$PX_FREESTYLE_VERSION

echo "Building Pixate Freestyle Distribution v${VERSION}"

cd $PX_FREESTYLE_ROOT

git archive --format=zip --prefix=pixate-freestyle-android-$VERSION/ -9  -o $PX_FREESTYLE_ROOT/build/pixate-freestyle-android-$VERSION.zip HEAD
 
mkdir -p $PX_FREESTYLE_ROOT/build

zip $PX_FREESTYLE_ROOT/build/pixate-freestyle-android-$VERSION.zip  -d \*/.idea/\* \*/scripts/\*

