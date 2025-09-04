package com.borisov.raiffeisenspringai.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Role {

    USER("user"), SYSTEM("system"), ASSISTANT("assistant");

    @Getter
    private final String role;

    public static Role fromRoleName(String roleName) {
        return Arrays.stream(values()).filter(role -> role.role.equals(roleName)).findAny().orElseThrow();
    }

}
