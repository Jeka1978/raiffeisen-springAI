package com.borisov.raiffeisenspringai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RaiffeisenSpringAiApplication {

    @Value("${max}")
    private long max;

    @Autowired
    private ChatRepo chatRepo;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultOptions(
                OllamaOptions.builder().topP(0.9).topK(40).repeatPenalty(1.1).temperature(0.7).build())
                .defaultAdvisors(
                        getHistoryAdvisor(),
                        SimpleLoggerAdvisor.builder().build())

                .build();

    }


    private  MessageChatMemoryAdvisor getHistoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                PosgressChatMemoryRepository.builder()
                        .chatMemoryRepository(chatRepo)
                        .maxMessages(max)
                        .build()
        ).build();
    }


    public static void main(String[] args) {


        ConfigurableApplicationContext context = SpringApplication.run(RaiffeisenSpringAiApplication.class, args);
        ChatClient chatClient = context.getBean(ChatClient.class);
//        String answer = chatClient.prompt("дай первую строчку Bohemian Rhapsody").call().content();
//        System.out.println(answer);
    }

}
