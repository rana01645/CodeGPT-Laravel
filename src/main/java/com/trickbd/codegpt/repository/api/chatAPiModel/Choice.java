package com.trickbd.codegpt.repository.api.chatAPiModel;

public class Choice {
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
