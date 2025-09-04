package com.borisov.raiffeisenspringai;

import com.borisov.raiffeisenspringai.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<Chat, Long> {
}
