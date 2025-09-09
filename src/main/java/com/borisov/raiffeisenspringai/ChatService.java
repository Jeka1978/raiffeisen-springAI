package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.Chat;
import com.borisov.raiffeisenspringai.model.ChatEntry;
import com.borisov.raiffeisenspringai.model.Role;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.borisov.raiffeisenspringai.model.Role.ASSISTANT;
import static com.borisov.raiffeisenspringai.model.Role.USER;

@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatService myProxy;

    public List<Chat> getAllChats() {
        return chatRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Chat findChat(long chatId) {
        return chatRepo.findById(chatId).orElseThrow();
    }

    public Chat createNewChat(String title) {
        return chatRepo.save(Chat.builder().title(title).build());
    }

    public void delete(long chatId) {
        chatRepo.deleteById(chatId);
    }

    public void proceedInteraction(long chatId, String prompt) {

        myProxy.addChatEntry(chatId, prompt, USER);
        String answer = chatClient.prompt().user(prompt).advisors().call().content();
        myProxy.addChatEntry(chatId, answer, ASSISTANT);
    }

    @Transactional
    public void addChatEntry(long chatId, String prompt, Role role) {
        Chat chat = chatRepo.findById(chatId).orElseThrow();
        chat.addEntry(prompt, role);
    }

    public SseEmitter proceedInteractionWithStreaming(long chatId, String userPrompt) {
        SseEmitter emitter = new SseEmitter(0L);

        chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(userPrompt).stream().chatResponse()
                .subscribe(response -> processToken(response, emitter),
                        emitter::completeWithError,
                        emitter::complete
                );

        return emitter;
    }

    @SneakyThrows
    private void processToken(ChatResponse response, SseEmitter emitter) {
        emitter.send(response.getResult().getOutput());
    }


}










