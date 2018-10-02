package pt.ulisboa.tecnico.cnv.renderfarm.mss.controllers;

import java.util.HashMap;
import java.util.Map;

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
public class MetricsDB {
	private static MetricsDB instance = null;
	private AmazonDynamoDBClient dynamoDB;
	private String tableName = "cnv-project-metrics";

	private MetricsDB() throws Exception {

		dynamoDB = new AmazonDynamoDBClient(AWS.getCredentials());
		Region euWest1 = Region.getRegion(AWS.REGION);
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


	public static MetricsDB getInstance() {
		try{
			if(instance == null) {
				instance = new MetricsDB();
			}
			return instance;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	public   ArrayList<DataMetrics> getItem(String value){
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition condition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue(value));
		scanFilter.put("file", condition);
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult =  dynamoDB.scan(scanRequest);
		//System.out.println("Result: " + scanResult);
		List<Map<String,AttributeValue>> result=scanResult.	getItems();
		ArrayList<DataMetrics> dataList=  new ArrayList<DataMetrics>();
		for (Integer i = 0; i < result.size(); i++) {
			dataList.add( new DataMetrics(result.get(i).get("file").getS(), result.get(i).get("sc").getN(),  result.get(i).get("sr").getN(),  result.get(i).get("wc").getN(),  result.get(i).get("wr").getN(), result.get(i).get("coff").getN(),  result.get(i).get("roff").getN(), result.get(i).get("instructions").getN()));
		}
		return dataList;
	}

	public boolean addItem(DataMetrics data) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		String file=data.getFile();
		String sc=Long.toString(data.getSc());
		String sr=Long.toString(data.getSr());
		String wc=Long.toString(data.getWc());
		String wr=Long.toString(data.getWr());
		String coff=Long.toString(data.getCoff());
		String roff=Long.toString(data.getRoff());
		String instructions=Long.toString(data.getInstructions());
		item.put("id", new AttributeValue(file+sc+sr+wc+wr+coff+roff+instructions));
		item.put("file", new AttributeValue(file));
		item.put("sc", new AttributeValue().withN(sc));
		item.put("sr", new AttributeValue().withN(sr));
		item.put("wc",new AttributeValue().withN(wc));
		item.put("wr", new AttributeValue().withN(wr));
		item.put("coff", new AttributeValue().withN(coff));
		item.put("roff", new AttributeValue().withN(roff));
		item.put("instructions", new AttributeValue().withN(instructions));

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
