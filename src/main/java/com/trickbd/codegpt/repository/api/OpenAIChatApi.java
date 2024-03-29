package com.trickbd.codegpt.repository.api;

import com.google.gson.Gson;
import com.trickbd.codegpt.repository.api.chatAPiModel.ChatCompletionRequest;
import com.trickbd.codegpt.repository.api.chatAPiModel.ChatCompletionResponse;
import com.trickbd.codegpt.repository.api.chatAPiModel.ChatMessageRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class OpenAIChatApi {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static OpenAIChatApi instance;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String bearerToken;

    public OpenAIChatApi(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public static OpenAIChatApi getInstance(String apiKey) {
        if (instance == null) {
            instance = new OpenAIChatApi(apiKey);
        }

        return instance;
    }

    public ChatCompletionResponse sendChatCompletionRequest(String model, ChatMessageRequest[] messages) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new Gson();
        String json = gson.toJson(new ChatCompletionRequest(model, messages));

        RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, json);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + bearerToken)
                .addHeader(CONTENT_TYPE_HEADER, "application/json")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IOException("Response body is null");
        }
        try {
            json = responseBody.string();
            return gson.fromJson(json, ChatCompletionResponse.class);
        } finally {
            responseBody.close();
        }
    }

    public CompletableFuture<ChatCompletionResponse> sendChatCompletionRequestAsync(String model, ChatMessageRequest[] messages) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendChatCompletionRequest(model, messages);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }
}
