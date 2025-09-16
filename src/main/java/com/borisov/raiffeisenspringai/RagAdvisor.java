package com.borisov.raiffeisenspringai;

import lombok.Builder;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.borisov.raiffeisenspringai.ExpansionQueryAdvisor.EXPANSION_QUERY;

@Builder
public class RagAdvisor implements BaseAdvisor {


    @Builder.Default
    private static final PromptTemplate template = PromptTemplate.builder().template("""
            CONTEXT: {context}
            Question: {question}
            """).build();

    @Builder.Default
    private int topK = 4;

    @Builder.Default

    private double similarity = 0.63;
    @Getter
    private int order;

    private VectorStore vectorStore;


    public static RagAdvisorBuilder builder(VectorStore vectorStore) {
        return new RagAdvisorBuilder().vectorStore(vectorStore);
    }


    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String originalUserQuery = chatClientRequest.prompt().getUserMessage().getText();
        String queryToRag = chatClientRequest.context().getOrDefault(EXPANSION_QUERY, originalUserQuery).toString();
        SearchRequest searchRequest = SearchRequest.builder().topK(topK*2).similarityThreshold(similarity).query(queryToRag).build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        BM25RerankEngine bm25RerankEngine = BM25RerankEngine.builder().build();
        documents = bm25RerankEngine.rerank(documents,queryToRag,topK);
        String llmContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String finalUserPrompt = template.render(Map.of("context", llmContext, "question", originalUserQuery));

        return chatClientRequest.mutate().prompt(chatClientRequest.prompt().augmentUserMessage(finalUserPrompt)).build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }


}
