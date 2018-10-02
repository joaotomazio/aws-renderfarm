
1. Instances => Instances

2. Launch Instance

3. `Amazon Linux AMI 2017.03.0 (HVM), SSD Volume Type` => Select

4. `t2.micro` => Configure Instance Details

5. Configure Instance Details

	* Number of instances: 1
	* Subnet: subnet-626a7014 eu-west-1a
	* Enable CloudWatch detailed monitoring

6. Add Storage

7. Configure Security Group

	* Select an existing security group: `cnv-project-ec2`

8. Review

9. Launch

	* Choose an existing key pair
	* Select a key pair: `My KeyPair`
		* I acknowledge that I have access to the selected private key file, and that without this file, I won't be able to log into my instance.
