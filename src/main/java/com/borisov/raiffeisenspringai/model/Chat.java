package com.borisov.raiffeisenspringai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    @OrderBy("createdAt DESC ")
    private List<ChatEntry> history;

    public void addEntry(String prompt, Role role) {
        history.add(ChatEntry.builder().content(prompt).role(role).build());
    }

    public void addEntry(ChatEntry chatEntry) {
        history.add(chatEntry);
    }
}
