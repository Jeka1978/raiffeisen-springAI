package com.borisov.raiffeisenspringai.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Role {

    USER("user") {
        @Override
        Message toMessage(String prompt) {
            return new UserMessage(prompt);
        }
    }, SYSTEM("system") {
        @Override
        Message toMessage(String prompt) {
            return new SystemMessage(prompt);
        }
    }, ASSISTANT("assistant") {
        @Override
        Message toMessage(String prompt) {
            return new AssistantMessage(prompt);
        }
    };

    @Getter
    private final String role;

    public static Role fromRoleName(String roleName) {
        return Arrays.stream(values()).filter(role -> role.role.equals(roleName)).findAny().orElseThrow();
    }

    abstract Message toMessage(String prompt);
}
