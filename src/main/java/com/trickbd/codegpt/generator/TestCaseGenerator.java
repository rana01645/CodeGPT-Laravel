package com.trickbd.codegpt.generator;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.trickbd.codegpt.ui.Notifier;
import com.trickbd.codegpt.helper.TestParser;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.repository.data.FileManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.ui.ProgressTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class TestCaseGenerator {
    String apiKey;
    String contents;

    VirtualFile file;

    AnActionEvent e;

    public TestCaseGenerator(String apiKey, String contents, VirtualFile file, AnActionEvent e) {
        this.apiKey = apiKey;
        this.contents = contents;
        this.file = file;
        this.e = e;
    }

    public void generateTestCase() {

        OpenAIChatApi api = new OpenAIChatApi(apiKey);

        String model = "gpt-3.5-turbo";
        OpenAIChatApi.ChatMessageRequest[] messages = {
                new OpenAIChatApi.ChatMessageRequest("user", "generate a PHP Laravel test class for this\n" + contents)
        };

        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        ProgressTask progressTask = new ProgressTask("Test Case Generator", "Generating test cases...", futureResponse);
        ProgressManager.getInstance().run(progressTask);

        futureResponse.thenAccept(response -> {
            // Handle successful response
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();


                String content = message.getContent();
                String filename = TestParser.parseFilename(content, file.getName());
                String directory = TestParser.parseDirectory(content);

                ApplicationManager.getApplication().invokeLater(() -> {
                    Project project = e.getProject();
                    assert project != null;
                    AtomicReference<VirtualFile> generatedFile = new AtomicReference<>();
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        try {
                            generatedFile.set(FileManager.saveFile(directory, filename, content.trim(), project));
                        } catch (IOException ex) {
                            System.out.println("error" + ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                    });
                    // Show a notification to indicate that the action was successful
                    Notifier.notifyAndOpenFIle("Test case generated", "A test case was generated for " + file.getName(), generatedFile.get().getPath(), project);
                });
            }

        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }
}
