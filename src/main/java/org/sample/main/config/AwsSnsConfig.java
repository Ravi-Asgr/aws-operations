package org.sample.main.config;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.SnsClient;

import java.io.File;

@Configuration
public class AwsSnsConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Bean
    public SnsAsyncClient getSnsAsyncClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SnsAsyncClient.builder().region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider).build();
    }
}
