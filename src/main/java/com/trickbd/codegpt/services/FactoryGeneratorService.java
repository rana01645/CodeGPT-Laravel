package com.trickbd.codegpt.services;

import java.util.concurrent.CompletableFuture;

public interface FactoryGeneratorService {
    CompletableFuture<String> generateFactory(String model,String modelName, String code);
}
