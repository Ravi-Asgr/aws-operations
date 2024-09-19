package org.sample.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.endpoints.DynamoDbEndpointProvider;

@Configuration
public class DynamoDbConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.dynamodb.endpoint}")
    private String awsDynamoDBEndPoint;


    @Bean
    public DynamoDbClient amazonAWSCredentials() {
        //DynamoDbClient.builder().endpointProvider(DynamoDbEndpointPr)

        return DynamoDbClient.builder().credentialsProvider(
                StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                )
        )
                .region(Region.of(region))
                .build();
    }
}
