package com.trickbd.codegpt.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ProgressTask extends Task.Backgroundable {
    private final CompletableFuture<?> futureResponse;
    private final String message;

    private final String title;

    public ProgressTask(@NotNull String title, @NotNull String message, CompletableFuture<?> futureResponse) {
        super(null, title);
        this.title = title;
        this.futureResponse = futureResponse;
        this.message = message;
    }

@Override
public void run(@NotNull ProgressIndicator indicator) {
    // Show the progress indicator
    indicator.setIndeterminate(true);
    indicator.setText(title);
    indicator.setText2(message);

    if (futureResponse == null) {
        indicator.setIndeterminate(false);
        indicator.setText("Done");
        return;
    }
    // Wait for the future to complete or for a cancellation request
      while (!futureResponse.isDone() && !indicator.isCanceled()) {
        indicator.checkCanceled();
    }

    // Cancel the future if a cancellation request is detected
    if (indicator.isCanceled()) {
        futureResponse.cancel(true);
    }
}

}
