package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.Chat;
import com.borisov.raiffeisenspringai.model.ChatEntry;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public interface ChatRepo extends JpaRepository<Chat, Long> {
    @Override
    default List<String> findConversationIds() {
        return findAll()
                .stream()
                .map(Chat::getId)
                .map(String::valueOf)
                .toList();
    }

    @Override
    default List<Message> findByConversationId(String conversationId) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getHistory().stream()
                .map(ChatEntry::toMessage)
                .toList();

    }

    @Override
    default void saveAll(String conversationId, List<Message> messages) {
        Chat chat = findById(Long.valueOf(conversationId)).orElseThrow();
        messages.stream()
                .map(ChatEntry::fromMessage)
                .forEach(chat::addEntry);
        save(chat);
    }

    @Override
   default void deleteByConversationId(String conversationId){
        //not implemented NEVER
    }
}
