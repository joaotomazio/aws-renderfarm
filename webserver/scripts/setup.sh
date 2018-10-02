# update instance
sudo yum -y update

# install dependencies
sudo yum -y install unzip
sudo yum -y install java-1.7.0-openjdk-devel.x86_64

# setup BIT
unzip BIT.zip
source BIT/java-config.sh

# setup RAYTRACER
mkdir raytracer
tar zxvf raytracer.tgz -C raytracer --strip-components=1
make -C raytracer

# instrument RAYTRACER
source instrumentation.sh

# create output folder for rendered images
mkdir output
sudo chmod -R 777 output

# aws credentials
mkdir ~/.aws
mv credentials ~/.aws/

# setup startup file
sudo sh -c 'cat rc.local > /etc/rc.local'
