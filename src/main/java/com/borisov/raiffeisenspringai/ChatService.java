package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepo;

    public List<Chat> getAllChats() {
        return chatRepo.findAll();
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
}
