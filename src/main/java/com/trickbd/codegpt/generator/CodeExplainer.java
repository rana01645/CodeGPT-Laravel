package com.trickbd.codegpt.generator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.ui.ProgressTask;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class CodeExplainer {
    String apiKey;
    String contents;


    AnActionEvent e;

    public CodeExplainer(String apiKey, String contents, AnActionEvent e) {
        this.apiKey = apiKey;
        this.contents = contents;
        this.e = e;
    }

    public void explain() {

        OpenAIChatApi api = new OpenAIChatApi(apiKey);

        String model = "gpt-3.5-turbo";
        OpenAIChatApi.ChatMessageRequest[] messages = {
                new OpenAIChatApi.ChatMessageRequest("user", "Explain this Code\n" + contents)
        };

        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        ProgressTask progressTask = new ProgressTask("Code explainer", "Generating code explanation...", futureResponse);
        ProgressManager.getInstance().run(progressTask);

        futureResponse.thenAccept(response -> {
            // Handle successful response
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();

                String content = message.getContent();

                ApplicationManager.getApplication().invokeLater(() -> {
                    showRightPanel(content);
                });
            }
        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }

    private void showRightPanel(String explained) {

        Project project = e.getProject();
        assert project != null;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        // Find or create the ToolWindow
        ToolWindow toolWindow = toolWindowManager.getToolWindow("CodeGPT - Code Explainer");
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow("CodeGPT - Code Explainer", false,
                    ToolWindowAnchor.RIGHT);
        }
        // Create the Content
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JTextArea textArea = new JTextArea(explained);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove margin
        scrollPane.setPreferredSize(toolWindow.getContentManager().getComponent().getSize());
        Content content = contentFactory.createContent(scrollPane, null, false);
        // Add the Content to the ToolWindow
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
        // Show the ToolWindow
        toolWindow.show(null);
    }
}
