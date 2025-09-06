package com.borisov.raiffeisenspringai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Message toMessage() {
        return role.toMessage(content);
    }

    public static ChatEntry fromMessage(Message message) {
        return ChatEntry.builder()
                .role(Role.fromRoleName(message.getMessageType().getValue()))
                .content(message.getText())
                .build();
    }
}
