#!/bin/sh

exec 3>&1
set -x

echo "Running raytracer..."

# raytracer flags
export LC_CTYPE="UTF-8"
export _JAVA_OPTIONS="-XX:-UseSplitVerifier "$_JAVA_OPTIONS

HOMEFOLDER="/home/ec2-user"
CLASSES="$HOMEFOLDER:$HOMEFOLDER/BIT:$HOMEFOLDER/raytracer-inst"

java -Djava.awt.headless=true -cp $CLASSES raytracer.Main $1 $2 $3 $4 $5 $6 $7 $8
