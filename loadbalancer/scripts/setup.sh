# update instance
sudo yum -y update

# install java jdk 1.7
sudo yum -y install java-1.7.0-openjdk-devel.x86_64

# aws credentials
mkdir ~/.aws
mv credentials ~/.aws/

# setup startup file
sudo sh -c 'cat rc.local > /etc/rc.local'

# start the LoadBalancer
sudo java -jar /home/ec2-user/loadbalancer.jar
