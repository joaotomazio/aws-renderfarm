package pt.ulisboa.tecnico.cnv.renderfarm.mss.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import pt.ulisboa.tecnico.cnv.renderfarm.mss.AWS;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import java.util.List;
import java.util.ArrayList;

public class TimeDB {
	private static TimeDB instance= null;
	private AmazonDynamoDBClient dynamoDB;
	private String tableName = "cnv-project-time";

	private TimeDB() throws Exception {

		dynamoDB = new AmazonDynamoDBClient(AWS.getCredentials());
		Region euWest1 = Region.getRegion(Regions.EU_WEST_1 );
		dynamoDB.setRegion(euWest1);
		// Create a table with a primary hash key named 'name', which holds a string
		CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
		.withKeySchema(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH))
		.withAttributeDefinitions(new AttributeDefinition().withAttributeName("id").withAttributeType(ScalarAttributeType.S))
		.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

		// Create table if it does not exist yet
		TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
		// wait for the table to move into ACTIVE state
		TableUtils.waitUntilActive(dynamoDB, tableName);

		// Describe our new table
		DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
		TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
		//System.out.println("Table Description: " + tableDescription);

	}

	public static TimeDB getInstance() {
		try{
			if(instance == null) {
				instance = new TimeDB();
			}
			return instance;
		}
		catch(Exception e){
			return null;
		}
	}

	public ArrayList<TimeMetrics> getItems(){
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		Long limit = (new Date()).getTime() - 600000;
		Condition condition = new Condition()
		.withComparisonOperator(ComparisonOperator.GE.toString())
		.withAttributeValueList(new AttributeValue(Long.toString(limit)));
		//System.out.println("NOW: " + (new Date()).getTime());
		//System.out.println("LIMIT: " + limit);
		scanFilter.put("id", condition);
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult =  dynamoDB.scan(scanRequest);
		//System.out.println("Result: " + scanResult);
		List<Map<String,AttributeValue>> result=scanResult.	getItems();
		ArrayList<TimeMetrics> dataList=  new ArrayList<TimeMetrics>();
		for (Integer i = 0; i < result.size(); i++) {
			//System.out.println(result.get(i).get("instructions").getN());
			dataList.add( new TimeMetrics(result.get(i).get("instructions").getN().toString(), result.get(i).get("time").getN().toString()));
		}
		return dataList;
	}

	public boolean addItem( String instructions, String time) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", new AttributeValue(String.valueOf((new Date()).getTime())));
		item.put("instructions", new AttributeValue().withN(instructions));
		item.put("time", new AttributeValue().withN(time));
		try {
			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			dynamoDB.putItem(putItemRequest);
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
			+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			return false;
		}
		return true;
	}

}
