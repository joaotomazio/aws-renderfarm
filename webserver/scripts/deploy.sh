# Configuration
IP="34.251.222.57"

# Create JAR
mvn clean compile assembly:single

# Send code to server
mv target/webserver-1.0-SNAPSHOT-jar-with-dependencies.jar target/webserver.jar
scp target/webserver.jar ec2-user@$IP:
scp scripts/raytracer/{BIT.zip,Instrumentation.java,instrumentation.sh,raytracer.tgz,raytracer.sh} ec2-user@$IP:

# Send script to server
scp scripts/{setup.sh,rc.local} ec2-user@$IP:

# Send credentials to server
scp ../mss/scripts/credentials ec2-user@$IP:

echo "Run setup.sh on server!"
