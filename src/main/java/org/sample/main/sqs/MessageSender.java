package org.sample.main.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.sample.main.Model.Department;
import org.sample.main.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class MessageSender {

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String destination;

    private ObjectMapper objectMapper = new ObjectMapper();

    //@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() throws Exception {

        String payload = objectMapper.writeValueAsString(generateUser());
        Map<String, MessageAttributeValue> attributeValueMap = Map.of("custom",
                MessageAttributeValue.builder().dataType("String").stringValue("attribute").build());
        SendMessageResponse sendMessageResponse = sqsAsyncClient.sendMessage(
                sqsSendOptions -> sqsSendOptions
                        .queueUrl(destination)
                        .messageAttributes(attributeValueMap)
                        .messageBody(payload)).get();
        System.out.println("Sent message to Queue. \n\t>> Message Id : " + sendMessageResponse.messageId()
                + "\n\t>> Metadata : " + sendMessageResponse.responseMetadata());
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
