# Configuration
IP="52.213.62.87"

# Create JAR
mvn clean compile assembly:single

# Send code to server
mv target/loadbalancer-1.0-SNAPSHOT-jar-with-dependencies.jar target/loadbalancer.jar
scp target/loadbalancer.jar ec2-user@$IP:

# Send setup.sh to server
scp scripts/{setup.sh,rc.local} ec2-user@$IP:

# Send credentials to server
scp ../mss/scripts/credentials ec2-user@$IP:

echo "Run setup.sh on server!"
