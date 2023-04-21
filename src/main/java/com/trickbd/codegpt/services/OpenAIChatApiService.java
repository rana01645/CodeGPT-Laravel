package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.OpenAIChatApi;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatApiService implements OpenAIChatService {
    private final OpenAIChatApi api;

    public OpenAIChatApiService(OpenAIChatApi api) {
        this.api = api;
    }

    @Override
    public CompletableFuture<String> sendChatRequest(String model, OpenAIChatApi.ChatMessageRequest[] messages) {
        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        return futureResponse.thenApply(response -> {
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();
                return message.getContent();
            }
            return null;
        }).exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            return null;
        });
    }
}
