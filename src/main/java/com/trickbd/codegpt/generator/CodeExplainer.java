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
import com.trickbd.codegpt.services.CodeExplanationService;
import com.trickbd.codegpt.ui.ProgressTask;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class CodeExplainer {

    private final CodeExplanationService codeExplanationService;


    AnActionEvent e;

    public CodeExplainer(CodeExplanationService codeExplanationService, AnActionEvent e) {
        this.codeExplanationService = codeExplanationService;
        this.e = e;
    }

    public void explain(String model, String contents) {
        CompletableFuture<String> futureResponse = codeExplanationService.explainCode(model, contents);

        ProgressTask progressTask = new ProgressTask("Code explainer", "Generating code explanation...", futureResponse);
        ProgressManager.getInstance().run(progressTask);

        futureResponse.thenAccept(explained -> {
            // Handle successful response
            ApplicationManager.getApplication().invokeLater(() -> {
                showRightPanel(explained);
            });

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
        ToolWindow toolWindow = toolWindowManager.getToolWindow("LaraGPT - Code Explainer");
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow("LaraGPT - Code Explainer", false,
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
