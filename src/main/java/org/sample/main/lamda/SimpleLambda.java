package org.sample.main.lamda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sample.main.App;
import org.sample.main.Model.User;
import org.sample.main.dynamodb.DBOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;

public final class SimpleLambda {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ConfigurableApplicationContext applicationContext;
    private static String appName;

    private static DBOperations dbOperations;

    static {
        applicationContext = SpringApplication.run(App.class);
        appName = applicationContext.getEnvironment().getProperty("spring.application.name");
    }

    public static void handleRequest(SQSEvent sqsEvent, Context context) throws Exception {
        System.out.println("Application Context " + applicationContext);
        dbOperations = (DBOperations) applicationContext.getBean("dbOperations");
        System.out.println("DBOperations beans " + dbOperations);
        List<SQSEvent.SQSMessage> messages = sqsEvent.getRecords();
        context.getLogger().log("Message count " + messages.size());

        for(SQSEvent.SQSMessage msg : messages) {
            context.getLogger().log("Message body: " + msg.getBody());
            String userString = msg.getBody();
            User user = objectMapper.readValue(userString, User.class);
            System.out.println("Constructed User " + user.toString());
            //listTables(user);
        }
    }

    public static void listTables(User user) {
        DynamoDbClient dynamoDbClient = amazonAWSCredentials();
        ListTablesRequest listTablesRequest = ListTablesRequest.builder().build();
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();
        listTablesResponse.tableNames().stream().forEach( a -> System.out.println(a));

        HashMap<String, AttributeValue> itemValues = new HashMap<String,AttributeValue>();

        // Add all content to the table
        itemValues.put("user_id", AttributeValue.builder().n(user.getId().toString()).build());
        itemValues.put("name", AttributeValue.builder().s(user.getName()).build());

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

    public static DynamoDbClient amazonAWSCredentials() {
        return DynamoDbClient.builder().credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIATCKAPB4ERTBWMN5X",
                        "cmPROezP0eqUMK+h4P1xRUnPOsiZGpTjLKoHtRTC")))
                .region(Region.of("ap-south-1"))
                .build();
    }
}
