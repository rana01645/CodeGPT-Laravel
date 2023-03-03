package com.trickbd.codegpt.generator;

import com.trickbd.codegpt.helper.Notifier;
import com.trickbd.codegpt.helper.TestParser;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.repository.data.FileManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

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
        System.out.println("apiKey");
        System.out.println(apiKey);

        OpenAIChatApi api = new OpenAIChatApi(apiKey);

        String model = "gpt-3.5-turbo";
        OpenAIChatApi.ChatMessageRequest[] messages = {
                new OpenAIChatApi.ChatMessageRequest("user", "generate a PHP Laravel test class for this\n" + contents)
        };

        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        futureResponse.thenAccept(response -> {
            // Handle successful response
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();
                System.out.println(message.getRole() + ": " + message.getContent());


                String content = message.getContent();
                String filename = TestParser.parseFilename(content, file.getName());
                String directory = TestParser.parseDirectory(content);

                ApplicationManager.getApplication().invokeLater(() -> {
                    Project project = e.getProject();
                    assert project != null;
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        try {
                            FileManager.saveFile(directory, filename, content.trim(), project);
                        } catch (IOException ex) {
                            System.out.println("error" + ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                    });
                });
            }
            // Show a notification to indicate that the action was successful
            Notifier.notifySuccess("Test case generated", "A test case was generated for " + file.getName());
        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }
}
