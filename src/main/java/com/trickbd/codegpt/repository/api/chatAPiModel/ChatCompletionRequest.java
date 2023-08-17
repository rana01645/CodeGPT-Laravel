package com.trickbd.codegpt.repository.api.chatAPiModel;

public class ChatCompletionRequest {
    private final String model;
    private final ChatMessageRequest[] messages;

    public ChatCompletionRequest(String model, ChatMessageRequest[] messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public ChatMessageRequest[] getMessages() {
        return messages;
    }
}
