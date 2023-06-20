package com.trickbd.codegpt.repository.api.chatAPiModel;

public class ChatCompletionResponse {
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
