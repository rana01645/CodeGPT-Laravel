package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatCodeExplanationService implements CodeExplanationService {
    private final OpenAIChatService chatService;

    public OpenAIChatCodeExplanationService(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public CompletableFuture<String> explainCode(String model, String code) {
        ChatMessageRequest[] messages = {
                new ChatMessageRequest("user", "Explain this Code\n" + code)
        };

        return chatService.sendChatRequest(model, messages);
    }
}
