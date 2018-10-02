
## Launch configuration

1. Auto Scaling => Launch Configurations

2. Create Auto Scaling Group

3. Choose AMI => My AMIs => `cnv-project` image

4. Instance Type: `t2.micro`

5. Configure details

	Name: `cnv-project`

	Monitoring: `√ Enable CloudWatch detailed monitoring`

6. Add storage => ...

7. Configure Security Group

	* Select an existing security group: `cnv-project-ec2`

8. Review => Create launch configuration

9. Select KeyPair

	 Proceed without a KeyPair

	 √ I acknowledge that I will not be able to connect to this instance unless I already know the password built into this AMI

---

## Auto Scaling Group

1. Create an Auto Scaling Group with this Launch Configuration

2. Create Auto Scaling Group

	* Group name: `cnv-project`
	* Group size: `1`
	* Subnet: `subnet... | Default in eu-west-1a`
	* Load Balancing: `√ Receive traffic from one or more load balancers`
		* Classic Load Balancers: `cnv-project`
	* Health Check Type: `ELB`
		* Health Check Grace Period: `300`
		* Monitoring: `√ Enable CloudWatch detailed monitoring`

3. Configure Scaling policies

	* Use scaling policies to adjust the capacity of this group

	### Increase Group Size

	* Scale between `1` and `5` instances
	* Name: `Increase Group Size`
	* Add alarm
		* No notification
		* Whenever: `Average` of `CPU Utilization` is `>=` `60`
		* For at least: `1` consecutive periods of `1 Minute`
		* Name of alarm: `cnv-project-highcpu`
		* => Create alarm
	* Take the action: `Add` `1` `instances` when `60` <=CPUUtilization

	### Decrease Group Size

	* Scale between `1` and `5` instances
	* Name: `Increase Group Size`
	* Add alarm
		* No notification
		* Whenever: `Average` of `CPU Utilization` is `<` `40`
		* For at least: `1` consecutive periods of `1 Minute`
		* Name of alarm: `cnv-project-lowcpu`
		* => Create alarm
	* Take the action: `Add` `1` `instances` when `40` >=CPUUtilization


4. Configure Notification
	* No!

5. No tags

6. Review => Create Auto Scaling Group
