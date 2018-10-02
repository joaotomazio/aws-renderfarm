
## Security Group for EC2 Instances

1. Network & Security => Security Group

2. Create Security Group

	Name: `cnv-project-ec2`

	Description: `SSH + HTTP8000`

	Inbound Rules:

	```
	Type	  Protocol	    Post		Source
	============================================
	SSH 		- 	TCP 	- 	22 		- 	Anywhere  # ssh access to a instance
	Custom TCP 	- 	TCP 	- 	8000 	- 	Anywhere  # direct http access to a instance
	```

	Outbound Rules:

	```
	Type	   Protocol	     Post		Source
	============================================
	All 	- 	 All 	- 	0-65535  - 	Anywhere
	```

3. OK

## Security Group for Load Balancers

1. Network & Security => Security Group

2. Create Security Group

	Name: `cnv-project-lb`

	Description: `SSH + HTTP80`

	Inbound Rules:

	```
	Type	  Protocol	    Post		Source
	============================================
	SSH 	- 	TCP 	- 	 22 	- 	Anywhere  # ssh access to a instance
	HTTP 	- 	TCP 	- 	 80	 	- 	Anywhere  # direct http access to a instance
	```

	Outbound Rules:

	```
	Type	   Protocol	     Post		Source
	============================================
	All 	- 	 All 	- 	0-65535  - 	Anywhere
	```

3. OK
