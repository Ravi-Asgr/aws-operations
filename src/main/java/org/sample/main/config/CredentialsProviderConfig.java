package org.sample.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.profiles.ProfileFile;

import java.io.File;

@Configuration
public class CredentialsProviderConfig {

    @Value("${spring.cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Bean
    @ConditionalOnProperty(name = "credProvider", havingValue = "profile", matchIfMissing = false)
    public ProfileCredentialsProvider profileCredentialsProvider() {
        //http://www.java2s.com/example/java-api/com/amazonaws/auth/profile/profilecredentialsprovider/profilecredentialsprovider-2-0.html
        File configFile = new File(System.getProperty("user.home"), ".aws/credentials");
        ProfileFile pf = ProfileFile.builder().type(ProfileFile.Type.CREDENTIALS).content(configFile.toPath()).build();
        return ProfileCredentialsProvider.builder()
                .profileFile(pf).profileName("default").build();
    }

    @Bean
    @ConditionalOnProperty(name = "credProvider", havingValue = "static", matchIfMissing = false)
    public StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));
    }

    /*
    Create a IAM Role, with Use case as EC2 and Permission give "AdministratorAccess" or individual
    permissions like "AmazonSQSFullAccess"
    Associate the Role to EC2 instance during launch or for a running instance
     */
    @Bean
    @ConditionalOnProperty(name = "credProvider", havingValue = "instance", matchIfMissing = false)
    public InstanceProfileCredentialsProvider instanceProfileCredentialsProvider() {
        return InstanceProfileCredentialsProvider.builder().build();
    }

    @Bean
    @ConditionalOnProperty(name = "credProvider", havingValue = "default", matchIfMissing = false)
    public DefaultCredentialsProvider defaultCredentialsProvider() {
        //https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html
        //https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html
        //https://stackoverflow.com/questions/38447646/how-to-retrieve-temporary-aws-credentials-from-amazon-using-iam-role-associated
        return DefaultCredentialsProvider.create();
    }
}
