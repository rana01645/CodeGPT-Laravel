package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatTestGeneratorService implements TestGeneratorService {
    private final OpenAIChatService chatService;

    public OpenAIChatTestGeneratorService(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public CompletableFuture<String> generateTestCase(String model, String code) {
        ChatMessageRequest[] messages = {
                new ChatMessageRequest("user", "generate a PHP Laravel test class for this\n" + code)
        };

        return chatService.sendChatRequest(model, messages);
    }
}
