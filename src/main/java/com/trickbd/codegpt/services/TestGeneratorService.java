package com.trickbd.codegpt.services;

import java.util.concurrent.CompletableFuture;

public interface TestGeneratorService {
    CompletableFuture<String> generateTestCase(String model, String code);
}
