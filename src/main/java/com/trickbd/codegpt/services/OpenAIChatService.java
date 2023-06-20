package com.trickbd.codegpt.services;

import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;

import java.util.concurrent.CompletableFuture;

public interface OpenAIChatService {
    CompletableFuture<String> sendChatRequest(String model, ChatMessageRequest[] messages);
}
