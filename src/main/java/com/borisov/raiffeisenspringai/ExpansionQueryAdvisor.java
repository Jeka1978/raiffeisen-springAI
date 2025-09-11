package com.borisov.raiffeisenspringai;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.Map;

@Builder
public class ExpansionQueryAdvisor implements BaseAdvisor {

    @Getter
    private final int order;

    private static final PromptTemplate template = PromptTemplate.builder()
            .template("""
            Instruction: Расширь поисковый запрос, добавив наиболее релевантные термины.
            
            СПЕЦИАЛИЗАЦИЯ ПО SPRING FRAMEWORK:
            - Жизненный цикл Spring бинов: конструктор → BeanPostProcessor → PostConstruct → прокси → ContextListener
            - Технологии: Dynamic Proxy, CGLib, reflection, аннотации, XML конфигурация
            - Компоненты: BeanFactory, ApplicationContext, BeanDefinition, MBean, JMX
            - Паттерны: dependency injection, AOP, профилирование, перехват методов

            ПРАВИЛА:
            1. Сохрани ВСЕ слова из исходного вопроса
            2. Добавь МАКСИМУМ ПЯТЬ наиболее важных термина
            3. Выбирай самые специфичные и релевантные слова
            4. Результат - простой список слов через пробел

            СТРАТЕГИЯ ВЫБОРА:
            - Приоритет: специализированные термины
            - Избегай общих слов
            - Фокусируйся на ключевых понятиях

            ПРИМЕРЫ:
            "что такое спринг" → "что такое спринг фреймворк Java"
            "как создать файл" → "как создать файл документ программа"

            Question: {question}
            Expanded query:
            """).build();

    private ChatClient chatClient;

    public static ExpansionQueryAdvisor.ExpansionQueryAdvisorBuilder builder(ChatModel chatModel) {
        return new ExpansionQueryAdvisor.ExpansionQueryAdvisorBuilder().chatClient(
                ChatClient.builder(chatModel)
                        .defaultOptions(OllamaOptions.builder()
                                .temperature(0.0)
                                .repeatPenalty(1.0)
                                .topP(0.1)
                                .topK(1).build())
                        .build()
        );
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

        String originalUserPrompt = chatClientRequest.prompt().getUserMessage().getText();
        String expansionQueryAdvisor = chatClient.prompt()
                .user(template.render(Map.of("question", originalUserPrompt)))
                .call()
                .content();

        chatClientRequest.context().put("EXPANSION_QUERY_ADVISOR", expansionQueryAdvisor);


         return chatClientRequest.mutate()
                 .prompt(
                         chatClientRequest.prompt().augmentUserMessage(expansionQueryAdvisor))
                 .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }


}
