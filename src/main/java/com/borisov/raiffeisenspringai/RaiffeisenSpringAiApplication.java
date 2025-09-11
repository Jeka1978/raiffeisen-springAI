package com.borisov.raiffeisenspringai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RaiffeisenSpringAiApplication {

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatModel chatModel;


    private static final PromptTemplate MY_PROMPT_TEMPLATE = new PromptTemplate(
            "{query}\n\n" +
                    "---------------------\n" +
                    "{question_answer_context}\n" +
                    "---------------------\n\n" +
                    "Если ответ есть в тексте — напиши его. " +
                    "Если ответа в тексте нет, сначала напиши: \"Вообще-то я тут не за этим, но могу ответить.\" " +
                    "Потом — обязательно ответь на вопрос. " +
                    "Максимум два коротких предложения."
    );



    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultOptions(
                        OllamaOptions.builder().topP(0.7).topK(20).repeatPenalty(1.1).temperature(0.3).build())
                .defaultAdvisors(
                        getPaceAdvisor(0),
                        SimpleLoggerAdvisor.builder().order(1).build(),
                        getHistoryAdvisor(2),
                        getRagAdvisor(3),
                        SimpleLoggerAdvisor.builder().order(4).build())
                .build();

    }

    private Advisor getPaceAdvisor(int order) {
        return ExpansionQueryAdvisor.builder(chatModel)
                .order(order)
                .build();
    }

    private Advisor getRagAdvisor(int order) {
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(MY_PROMPT_TEMPLATE)
                .order(order)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.63)
                        .topK(4)
                        .build())
                .build();
        return questionAnswerAdvisor;
    }


    private MessageChatMemoryAdvisor getHistoryAdvisor(int order) {
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
