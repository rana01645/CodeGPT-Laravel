package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.OpenAIChatApi;

import java.util.concurrent.CompletableFuture;

public class OpenAIChatCodeExplanationService implements CodeExplanationService {
    private static OpenAIChatCodeExplanationService instance;
    private final OpenAIChatApi api;

    public OpenAIChatCodeExplanationService(OpenAIChatApi api) {
        this.api = api;
    }

    public static synchronized OpenAIChatCodeExplanationService getInstance(OpenAIChatApi api) {
        if (instance == null) {
            instance = new OpenAIChatCodeExplanationService(api);
        }
        return instance;
    }

    @Override
    public CompletableFuture<String> explainCode(String model, String code) {
        OpenAIChatApi.ChatMessageRequest[] messages = {
                new OpenAIChatApi.ChatMessageRequest("user", "Explain this Code\n" + code)
        };

        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        return futureResponse.thenApply(response -> {
            // Handle successful response
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();

                String content = message.getContent();

                return content;
            }
            return null;
        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }
}
