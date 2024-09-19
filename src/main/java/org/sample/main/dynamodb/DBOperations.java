package org.sample.main.dynamodb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;

@Service
public class DBOperations {

    @Autowired
    private DynamoDbClient dynamoDbClient;

    public void listTables() {
        ListTablesRequest listTablesRequest = ListTablesRequest.builder().build();
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();
        listTablesResponse.tableNames().stream().forEach( a -> System.out.println(a));

        HashMap<String, AttributeValue> itemValues = new HashMap<String,AttributeValue>();

        // Add all content to the table
        itemValues.put("id", AttributeValue.builder().n("1").build());
        itemValues.put("name", AttributeValue.builder().s("Ravi").build());

        PutItemRequest request = PutItemRequest.builder().tableName("User").item(itemValues).build();
        try {
            dynamoDbClient.putItem(request);
            System.out.println("User table was successfully updated");

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table not found");
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }
}
