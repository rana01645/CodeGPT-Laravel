package com.trickbd.codegpt.services;

import java.util.concurrent.CompletableFuture;

public interface CodeExplanationService {
    CompletableFuture<String> explainCode(String model, String code);
}
