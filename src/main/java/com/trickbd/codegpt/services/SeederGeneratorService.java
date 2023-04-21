package com.trickbd.codegpt.services;

import java.util.concurrent.CompletableFuture;

public interface SeederGeneratorService {
    CompletableFuture<String> generateSeeder(String model, String modelName);
}
