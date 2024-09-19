package org.sample.main.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.sample.main.Model.Department;
import org.sample.main.Model.User;
import org.sample.main.dynamodb.DBOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class SnsMessageSender {

    @Autowired
    SnsAsyncClient snsAsyncClient;

    @Value("${spring.cloud.aws.sns.endpoint}")
    private String snsArn;


    private ObjectMapper objectMapper = new ObjectMapper();

    //@Scheduled(fixedDelay = 8, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() throws Exception {

        String payload = objectMapper.writeValueAsString(generateUser());
        //snsTemplate.sendNotification(payload, "subject"); //works
        //snsTemplate.convertAndSend(payload); //works
        //snsTemplate.convertAndSend(snsArn, payload); //works
        //snsTemplate.sendNotification(snsArn, payload, "subject"); //works
        //dbOperations.listTables();
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("event", MessageAttributeValue.builder()
                .dataType("String")
                .stringValue("publishEvent")
                .build());
        PublishRequest pubReq = PublishRequest.builder().message(payload).topicArn(snsArn).build();
        PublishResponse publishResponse = snsAsyncClient.publish(pubReq).get();
        if (publishResponse.sdkHttpResponse().isSuccessful()) {
            System.out.println("Message published to Topic." + "\n\t" + ">> Message Id: " + publishResponse.messageId());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    publishResponse.sdkHttpResponse().statusText().get());
        }

    }

    private User generateUser() {
        return User.builder().id(new Random().nextLong()).name(generateRandomString()).department(
                Department.builder().depId(new Random().nextLong()).depName(generateRandomString()).build()
        ).build();
    }

    private String generateRandomString() {
        return new Random().ints(97,122).limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
