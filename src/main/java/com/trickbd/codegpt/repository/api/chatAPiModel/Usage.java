package com.trickbd.codegpt.repository.api.chatAPiModel;

public class Usage {
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
