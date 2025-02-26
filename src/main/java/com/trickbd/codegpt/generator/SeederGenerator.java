package com.trickbd.codegpt.generator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.services.FactoryGeneratorService;
import com.trickbd.codegpt.services.SeederGeneratorService;
import com.trickbd.codegpt.ui.Notifier;
import com.trickbd.codegpt.ui.ProgressTask;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class SeederGenerator {
    FactoryGeneratorService factoryGeneratorService;
    SeederGeneratorService seederGeneratorService;
    private final AnActionEvent e;
    private FileManager fileManager;

    public SeederGenerator(FactoryGeneratorService factoryGeneratorService,
                           SeederGeneratorService seederGeneratorService, AnActionEvent e, FileManager fileManager) {
        this.factoryGeneratorService = factoryGeneratorService;
        this.seederGeneratorService = seederGeneratorService;
        this.e = e;
        this.fileManager = fileManager;

    }

    public void generateFactoryAndSeeder(String model, String modelName, String contents) {

        CompletableFuture<String> factoryFutureResponse = factoryGeneratorService.generateFactory(model, modelName, contents);

        ProgressTask progressTask = new ProgressTask("Model Factory Generator", "Generating " + modelName + " factory...", factoryFutureResponse);
        ProgressManager.getInstance().run(progressTask);

        factoryFutureResponse.thenAccept(factoryContent -> {
            // Handle successful response

            ApplicationManager.getApplication().invokeLater(() -> {
                factoryGenerated(modelName, factoryContent);
            });

            System.out.println("Factory generated");

            String seederContent = seederGeneratorService.generateSeeder(modelName);
            ApplicationManager.getApplication().invokeLater(() -> {
                seederGenerated(modelName, seederContent);
            });

        }).exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            // Handle exception
            return null;
        });
    }

    private void seederGenerated(String modelName, String seederContent) {
        String seederFileName = modelName + "Seeder.php";
        String directory = "database/seeders";
        Project project = e.getProject();
        assert project != null;

        AtomicReference<VirtualFile> seederFile = new AtomicReference<>();

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (seederContent != null) {
                    seederFile.set(fileManager.saveFile(directory, seederFileName, seederContent.trim(), project));
                }
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        });

        // Show a notification to indicate that the action was successful
        if (seederFile.get() != null) {
            Notifier.notifyAndOpenFIle(modelName + " seeder generated", "A model seeder was generated for " + modelName, "Open Seeder File", seederFile.get().getPath(), project);
        }
    }

    private void factoryGenerated(String modelName, String factoryContent) {
        String factoryFileName = modelName + "Factory.php";
        String directory = "database/factories";
        Project project = e.getProject();
        assert project != null;

        AtomicReference<VirtualFile> factoryFile = new AtomicReference<>();

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (factoryContent != null) {
                    factoryFile.set(fileManager.saveFile(directory, factoryFileName, factoryContent.trim(), project));
                }
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        });

        // Show a notification to indicate that the action was successful
        if (factoryFile.get() != null) {
            Notifier.notifyAndOpenFIle(modelName + " factory generated", "A factory was generated for " + modelName, "Open Factory file", factoryFile.get().getPath(), project);
        }
    }

}
