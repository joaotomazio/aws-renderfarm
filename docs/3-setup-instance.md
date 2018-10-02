0. Install RenderFarm parent project

	```
	mvn install
	```

1. Install MSS project

	```
	cd mss
	mvn install
	```

2. Deploy WebServer code

	```
	cd webserver
	sh scripts/deploy.sh  # edit IP address
	```

3. Access and setup created instance

	```
	ssh ec2-user@<instance-ip-address>
	```
	```
	source setup.sh
	```

4. Create image

	* Instances => Instances => Instance => Image => Create image

		Image name: `cnv-project`
		Image description: `WebServer`

5. Terminate instance

	* Instance State => Terminate
