package org.sample.main.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {

    @SqsListener("simple-queue")
    public void messageListener(Message<String> message) {
        //Custom attributes are available in Header
        System.out.println("Received message from Queue."
                + "\n\t" + ">> Payload : " + message.getPayload()
                + "\n\t" + ">> Custom Attribute : " + message.getHeaders().get("custom")
                + "\n\t" + ">> Attributes : " + message.getHeaders());
    }
}
