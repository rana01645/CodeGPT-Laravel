package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.*;
import com.trickbd.codegpt.repository.api.chatAPiModel.ChatCompletionResponse;
import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;
import com.trickbd.codegpt.repository.api.chatAPiModel.Choice;
import com.trickbd.codegpt.repository.api.chatAPiModel.Message;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatApiService implements OpenAIChatService {
    private final OpenAIChatApi api;

    public OpenAIChatApiService(OpenAIChatApi api) {
        this.api = api;
    }

    @Override
    public CompletableFuture<String> sendChatRequest(String model, ChatMessageRequest[] messages) {
        CompletableFuture<ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        return futureResponse.thenApply(response -> {
            for (Choice choice : response.getChoices()) {
                Message message = choice.getMessage();
                return message.getContent();
            }
            return null;
        }).exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            return null;
        });
    }
}
