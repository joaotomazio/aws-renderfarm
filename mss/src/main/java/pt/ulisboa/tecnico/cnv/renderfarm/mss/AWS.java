package pt.ulisboa.tecnico.cnv.renderfarm.mss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.regions.Regions;

public class AWS {

	public static final Regions REGION = Regions.EU_WEST_1;

	public static AWSCredentials getCredentials() throws AmazonClientException{
		try {
			ProfileCredentialsProvider profile = new ProfileCredentialsProvider("/home/ec2-user/.aws/credentials", "default");
			AWSCredentials credentials = profile.getCredentials();
			return credentials;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AmazonClientException(
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (~/.aws/credentials), and is in valid format.",
			e);
		}
	}

	public static HashSet<String> getEC2Instances() {
		AmazonEC2      ec2;
		HashSet<String> ips = new HashSet<String>();
		try {
			ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).withCredentials(new AWSStaticCredentialsProvider(getCredentials())).build();

			DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
			List<Reservation> reservations = describeInstancesRequest.getReservations();
			Set<Instance> instances = new HashSet<Instance>();

			for (Reservation reservation : reservations) {
				instances.addAll(reservation.getInstances());
			}
			for (Instance instance : instances) {

				if(instance.getState().getName().equals("running")){
					if(instance.getSecurityGroups().get(0).getGroupName().equals("cnv-project-ec2")){
						ips.add(instance.getPublicIpAddress());
					}
				}
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}
		return ips;
	}
}
