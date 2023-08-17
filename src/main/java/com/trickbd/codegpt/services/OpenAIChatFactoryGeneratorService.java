package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatFactoryGeneratorService implements FactoryGeneratorService {
    private final OpenAIChatService chatService;

    public OpenAIChatFactoryGeneratorService(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public CompletableFuture<String> generateFactory(String model,String modelName, String code) {
        ChatMessageRequest[] messages = {
                new ChatMessageRequest("user",
                        "generate a PHP Laravel factory class for this " + modelName +
                                " model with the following migration data (Give only full PHP code):\n" + code)
        };

        return chatService.sendChatRequest(model, messages);
    }
}
