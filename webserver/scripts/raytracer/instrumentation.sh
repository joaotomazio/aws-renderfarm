HOMEFOLDER="/home/ec2-user"

# export LC_CTYPE="UTF-8"
# source BIT/java-config.sh

rm -rf $HOMEFOLDER/BIT/examples
rm -rf $HOMEFOLDER/BIT/samples

export CLASSPATH="$CLASSPATH:$HOMEFOLDER/BIT:$HOMEFOLDER"
javac Instrumentation.java

# make clean -C raytracer-master
# make -C raytracer-master

mkdir -p $HOMEFOLDER/raytracer-inst/raytracer
mkdir -p $HOMEFOLDER/raytracer-inst/raytracer/shapes
mkdir -p $HOMEFOLDER/raytracer-inst/raytracer/pigments

java Instrumentation $HOMEFOLDER/raytracer/src/raytracer $HOMEFOLDER/raytracer-inst/raytracer
java Instrumentation $HOMEFOLDER/raytracer/src/raytracer/shapes $HOMEFOLDER/raytracer-inst/raytracer/shapes
java Instrumentation $HOMEFOLDER/raytracer/src/raytracer/pigments $HOMEFOLDER/raytracer-inst/raytracer/pigments

#java -Djava.awt.headless=true -cp $HOMEFOLDER/raytracer-inst:$CLASSPATH raytracer.Main $HOMEFOLDER/raytracer/test01.txt $HOMEFOLDER/output/example.bmp 1000 500 1000 500 40 40
