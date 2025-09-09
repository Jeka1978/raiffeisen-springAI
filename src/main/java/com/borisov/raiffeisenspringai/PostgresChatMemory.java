package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.Chat;
import com.borisov.raiffeisenspringai.model.ChatEntry;
import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Builder
public class PostgresChatMemory implements ChatMemory {

    private ChatRepo chatMemoryRepository;
    private int maxMessages;


    @Override
    public void add(String conversationId, List<Message> messages) {
        Chat chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream()
                .map(ChatEntry::fromMessage)
                .forEach(chat::addEntry);
        chatMemoryRepository.save(chat);

    }

    @Override
    public List<Message> get(String conversationId) {
        Chat chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        int messagesToSkip = Math.max(0, chat.getHistory().size() - maxMessages);
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .skip(messagesToSkip)
                .toList();
    }

    @Override
    public void clear(String conversationId) {

    }
}
