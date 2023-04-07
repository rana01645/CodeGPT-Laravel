package com.trickbd.codegpt.generator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.helper.ModelParser;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.repository.data.FileManager;
import com.trickbd.codegpt.ui.Notifier;
import com.trickbd.codegpt.ui.ProgressTask;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class SeederGenerator {
    private final String apiKey;
    private final String modelContents;
    private final String migrationContents;
    private final VirtualFile file;
    private final AnActionEvent e;

    public SeederGenerator(String apiKey, String modelContents, String migrationContents, VirtualFile file, AnActionEvent e) {
        this.apiKey = apiKey;
        this.modelContents = modelContents;
        this.migrationContents = migrationContents;
        this.file = file;
        this.e = e;
    }

    public void generateSeeder() {
        String modelName = ModelParser.parseModelName(modelContents);

        OpenAIChatApi api = new OpenAIChatApi(apiKey);

        String model = "gpt-3.5-turbo";
        OpenAIChatApi.ChatMessageRequest[] factoryMessages = {
                new OpenAIChatApi.ChatMessageRequest("user",
                        "generate a PHP Laravel factory class for this " + modelName +
                                " model with the following migration data (Give only full PHP code):\n" + migrationContents)
        };

        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> factoryFutureResponse = api.sendChatCompletionRequestAsync(model, factoryMessages);

        ProgressTask progressTask = new ProgressTask("Model Factory Generator", "Generating model factory...", factoryFutureResponse);
        ProgressManager.getInstance().run(progressTask);

        factoryFutureResponse.thenAccept(factoryResponse -> {
            // Handle successful response
            for (OpenAIChatApi.Choice factoryChoice : factoryResponse.getChoices()) {
                OpenAIChatApi.Message factoryMessage = factoryChoice.getMessage();
                String factoryContent = factoryMessage.getContent();
                System.out.println("Factory content: " + factoryContent);

                String factoryFileName = modelName + "Factory.php";
                String directory = "database/factories";

                ApplicationManager.getApplication().invokeLater(() -> {
                    Project project = e.getProject();
                    assert project != null;

                    AtomicReference<VirtualFile> factoryFile = new AtomicReference<>();

                    ApplicationManager.getApplication().runWriteAction(() -> {
                        try {
                            if (factoryContent != null) {
                                factoryFile.set(FileManager.saveFile(directory, factoryFileName, factoryContent.trim(), project));
                            }
                        } catch (IOException ex) {
                            System.out.println("Error: " + ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                    });

                    // Show a notification to indicate that the action was successful
                    if (factoryFile.get() != null) {
                        Notifier.notifyAndOpenFIle(
                                "Model factory generated",
                                "A model factory was generated for " + modelName,
                                "Open Factory file",
                                factoryFile.get().getPath(),
                                project
                        );
                    }
                });
            }

            OpenAIChatApi.ChatMessageRequest[] seederMessages = {
                    new OpenAIChatApi.ChatMessageRequest("user",
                            "generate a PHP Laravel seeder class where factory exists for this " + modelName +
                                    " model with the following migration data(Give only full PHP code):\n" + migrationContents)
            };

            CompletableFuture<OpenAIChatApi.ChatCompletionResponse>
                    seederFutureResponse = api.sendChatCompletionRequestAsync(model, seederMessages);

            ProgressTask seederProgressTask = new ProgressTask("Model Seeder Generator", "Generating model seeder...", seederFutureResponse);
            ProgressManager.getInstance().run(seederProgressTask);

            seederFutureResponse.thenAccept(seederResponse -> {
                // Handle successful response
                for (OpenAIChatApi.Choice seederChoice : seederResponse.getChoices()) {
                    OpenAIChatApi.Message seederMessage = seederChoice.getMessage();
                    String seederContent = seederMessage.getContent();
                    System.out.println("Seeder content: " + seederContent);


                    String seederFileName = modelName + "TableSeeder.php";
                    String directory = "database/seeders";

                    ApplicationManager.getApplication().invokeLater(() -> {
                        Project project = e.getProject();
                        assert project != null;

                        AtomicReference<VirtualFile> seederFile = new AtomicReference<>();

                        ApplicationManager.getApplication().runWriteAction(() -> {
                            try {
                                if (seederContent != null) {
                                    seederFile.set(FileManager.saveFile(directory, seederFileName, seederContent.trim(), project));
                                }
                            } catch (IOException ex) {
                                System.out.println("Error: " + ex.getMessage());
                                throw new RuntimeException(ex);
                            }
                        });

                        // Show a notification to indicate that the action was successful
                        if (seederFile.get() != null) {
                            Notifier.notifyAndOpenFIle(
                                    "Model seeder generated",
                                    "A model seeder was generated for " + modelName,
                                    "Open Seeder File",
                                    seederFile.get().getPath(),
                                    project
                            );
                        }
                    });
                }
            }).exceptionally(ex -> {
                System.out.println("Error: " + ex.getMessage());
                // Handle exception
                return null;
            });
        }).exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            // Handle exception
            return null;
        });
    }

}
