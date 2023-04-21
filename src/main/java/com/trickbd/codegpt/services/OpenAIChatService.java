package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.OpenAIChatApi;

import java.util.concurrent.CompletableFuture;

public interface OpenAIChatService {
    CompletableFuture<String> sendChatRequest(String model, OpenAIChatApi.ChatMessageRequest[] messages);
}
