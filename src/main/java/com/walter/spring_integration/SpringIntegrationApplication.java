package com.walter.spring_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Instant;
import java.util.Locale;

@SpringBootApplication
public class SpringIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIntegrationApplication.class, args);
    }

    @Bean
    MessageChannel messageChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    IntegrationFlow integrationFlow() {

        return IntegrationFlow
                .from((MessageSource<String>) () -> MessageBuilder.withPayload(getPayload()).build(), poller -> poller.poller(po -> po.fixedRate(1000)))
                .channel(messageChannel())
                .get();
    }

    @NonNull
    private String getPayload() {
        return Math.random() > .5 ? "Hello World @ " + Instant.now() + "," : "Hi @ " + Instant.now() + ",";
    }


    @Bean
    IntegrationFlow flow() {
        return IntegrationFlow
                .from(messageChannel())
                .filter((GenericSelector<String>) source -> source.contains("Hi"))
                .transform((GenericTransformer<String, String>) source -> source.toUpperCase(Locale.ROOT))
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Payload is " + payload +" with headers" + headers);

                    return null;
                })
                .get();
    }
}

class Test{
    public void test(String text){
        System.out.printf("The message is %s\n", text);
    }
}
