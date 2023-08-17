package com.trickbd.codegpt.repository.api.chatAPiModel;

public class ChatMessageRequest {
    private final String role;
    private final String content;

    public ChatMessageRequest(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
