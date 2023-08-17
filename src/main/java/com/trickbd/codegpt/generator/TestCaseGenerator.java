package com.trickbd.codegpt.generator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.helper.TestParser;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.services.TestGeneratorService;
import com.trickbd.codegpt.ui.Notifier;
import com.trickbd.codegpt.ui.ProgressTask;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class TestCaseGenerator {

    VirtualFile file;

    AnActionEvent e;

    private final TestGeneratorService testGeneratorService;

    public TestCaseGenerator(TestGeneratorService testGeneratorService, VirtualFile file, AnActionEvent e) {
        this.testGeneratorService = testGeneratorService;
        this.file = file;
        this.e = e;
    }

    public void generateTestCase(String model,String contents) {

        CompletableFuture<String> futureResponse = testGeneratorService.generateTestCase(model, contents);

        ProgressTask progressTask = new ProgressTask("Test Case Generator", "Generating test cases...", futureResponse);
        ProgressManager.getInstance().run(progressTask);

        futureResponse.thenAccept(content -> {
            // Handle successful response
            String filename = TestParser.parseFilename(content, file.getName());
            String directory = TestParser.parseDirectory(content);
            showFileLocation(filename, directory, content);

        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }

    private void showFileLocation(String filename, String directory, String content) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Project project = e.getProject();
            assert project != null;
            AtomicReference<VirtualFile> generatedFile = new AtomicReference<>();
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    generatedFile.set(FileManager.getInstance().saveFile(directory, filename, content.trim(), project));
                } catch (IOException ex) {
                    System.out.println("error" + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            });
            // Show a notification to indicate that the action was successful
            Notifier.notifyAndOpenFIle(
                    "Test case generated",
                    "A test case was generated for " + file.getName(),
                    "Open Test File",
                    generatedFile.get().getPath(),
                    project
            );
        });
    }
}
