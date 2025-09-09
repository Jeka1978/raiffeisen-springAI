package com.borisov.raiffeisenspringai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RaiffeisenSpringAiApplication {

    @Autowired
    private ChatRepo chatRepo;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultOptions(
                OllamaOptions.builder().topP(0.7).topK(20).repeatPenalty(1.1).temperature(0.3).build())
                .defaultAdvisors(
                        SimpleLoggerAdvisor.builder().order(1).build(),
                        getHistoryAdvisor(2),
                        SimpleLoggerAdvisor.builder().order(3).build())

                .build();

    }


    private  MessageChatMemoryAdvisor getHistoryAdvisor(int order) {
        return MessageChatMemoryAdvisor.builder(
                PostgresChatMemory.builder()
                        .chatMemoryRepository(chatRepo)
                        .maxMessages(4)

                        .build()
        ).order(order).build();
    }


    public static void main(String[] args) {


        ConfigurableApplicationContext context = SpringApplication.run(RaiffeisenSpringAiApplication.class, args);
        ChatClient chatClient = context.getBean(ChatClient.class);
//        String answer = chatClient.prompt("дай первую строчку Bohemian Rhapsody").call().content();
//        System.out.println(answer);
    }




}
