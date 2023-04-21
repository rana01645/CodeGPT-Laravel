package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.OpenAIChatApi;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatSeederGeneratorService implements SeederGeneratorService {
    private final OpenAIChatService chatService;

    public OpenAIChatSeederGeneratorService(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public CompletableFuture<String> generateSeeder(String model,String modelName) {
        OpenAIChatApi.ChatMessageRequest[] messages = {
                new OpenAIChatApi.ChatMessageRequest("user",
                            "generate a PHP Laravel seeder class where factory exists for this " + modelName + " " +
                                    "model (Give only PHP code and use laravel latest syntax like  Model::factory()->count(5)->create();," +
                                    " no additional text or instructions)")
        };

        return chatService.sendChatRequest(model, messages);
    }
}
