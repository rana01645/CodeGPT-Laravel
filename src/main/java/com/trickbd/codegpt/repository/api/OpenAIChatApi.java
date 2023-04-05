package com.trickbd.codegpt.repository.api;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class OpenAIChatApi {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String bearerToken;

    public OpenAIChatApi(String bearerToken) {
        this.bearerToken = bearerToken;
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


    public static class ChatCompletionRequest {
        private final String model;
        private final ChatMessageRequest[] messages;

        public ChatCompletionRequest(String model, ChatMessageRequest[] messages) {
            this.model = model;
            this.messages = messages;
        }

        public String getModel() {
            return model;
        }

        public ChatMessageRequest[] getMessages() {
            return messages;
        }
    }

    public static class ChatMessageRequest {
        private final String role;
        private final String content;

        public ChatMessageRequest(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static class ChatCompletionResponse {
        private final String id;
        private final String object;
        private final long created;
        private final String model;
        private final Usage usage;
        private final Choice[] choices;

        public ChatCompletionResponse(String id, String object, long created, String model, Usage usage, Choice[] choices) {
            this.id = id;
            this.object = object;
            this.created = created;
            this.model = model;
            this.usage = usage;
            this.choices = choices;
        }

        public String getId() {
            return id;
        }

        public String getObject() {
            return object;
        }

        public long getCreated() {
            return created;
        }

        public String getModel() {
            return model;
        }

        public Usage getUsage() {
            return usage;
        }

        public Choice[] getChoices() {
            return choices;
        }
    }

    public static class Usage {
        private final int prompt_tokens;
        private final int completion_tokens;
        private final int total_tokens;

        public Usage(int prompt_tokens, int completion_tokens, int total_tokens) {
            this.prompt_tokens = prompt_tokens;
            this.completion_tokens = completion_tokens;
            this.total_tokens = total_tokens;
        }

        public int getPromptTokens() {
            return prompt_tokens;
        }

        public int getCompletionTokens() {
            return completion_tokens;
        }

        public int getTotalTokens() {
            return total_tokens;
        }
    }

    public static class Choice {
        private final Message message;
        private final String finish_reason;
        private final int index;

        public Choice(Message message, String finish_reason, int index) {
            this.message = message;
            this.finish_reason = finish_reason;
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finish_reason;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
