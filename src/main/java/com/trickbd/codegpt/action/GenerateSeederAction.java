package com.trickbd.codegpt.action;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.constants.Constants;
import com.trickbd.codegpt.engines.MustacheTemplateEngine;
import com.trickbd.codegpt.engines.TemplateEngine;
import com.trickbd.codegpt.generator.SeederGenerator;
import com.trickbd.codegpt.helper.CaseChanger;
import com.trickbd.codegpt.helper.ModelFinder;
import com.trickbd.codegpt.helper.ModelParser;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.repository.data.file.FileManagerFactory;
import com.trickbd.codegpt.repository.data.local.LocalData;
import com.trickbd.codegpt.services.*;
import com.trickbd.codegpt.settings.SettingsPanel;
import com.trickbd.codegpt.ui.Notifier;

public class GenerateSeederAction extends AnAction {

    ModelParser modelParser;
    FileManager fileManager;
    ModelFinder modelFinder;

    public GenerateSeederAction() {
        fileManager = FileManagerFactory.createDefault();
        this.modelParser = new ModelParser(fileManager);
        this.modelFinder = new ModelFinder(fileManager);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get a reference to the current file
        VirtualFile fileName = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (fileName == null) {
            Notifier.notifyError("File not found", "File not found to generate migration and seeder");
            return;
        }

        Project project = e.getProject();
        if (project == null) {
            return;
        }


        // Check if the file is a Laravel model
        boolean isModel = modelParser.isLaravelModel(fileName);
        boolean isMigration = modelParser.isLaravelMigration(fileName);
        if (!isModel && !isMigration) {
            Notifier.notifyError("Invalid file", "Invalid file to generate migration and seeder");
            return;
        }

        String modelName;
        String migrationContents;

        if (isModel) {
            VirtualFile migrationFile;
            modelName = ModelParser.parseModelName(fileManager.readFile(fileName));
            migrationFile = modelFinder.findMigrationFileForModel(modelName, project, new CaseChanger());
            if (migrationFile == null) {
                Notifier.notifyError("Migration file not found", "Migration file not found for model " + modelName);
                return;
            }

            // Read the contents of the model and migration files
            migrationContents = fileManager.readFile(migrationFile);
        } else {
            // Read the contents of the model and migration files
            modelName = modelFinder.findModelNameForMigration(fileName, new CaseChanger());
            migrationContents = fileManager.readFile(fileName);
        }


        // Generate the seeder
        String apiKey = LocalData.getInstance(PropertiesComponent.getInstance()).get(Constants.API_KEY);
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    generateFactoryAndSeeder(e, modelName, migrationContents, apiKey1);
                }
            });
            settingsPanel.show();
            return;
        }
        generateFactoryAndSeeder(e, modelName, migrationContents, apiKey);

    }

    private void generateFactoryAndSeeder(AnActionEvent e, String modelName, String migrationContents, String apiKey) {
        OpenAIChatApi chatApi = new OpenAIChatApi(apiKey);
        OpenAIChatService chatService = new OpenAIChatApiService(chatApi);
        FactoryGeneratorService factoryGeneratorService = new OpenAIChatFactoryGeneratorService(chatService);

        TemplateEngine templateEngine = new MustacheTemplateEngine();
        SeederGeneratorService seederGeneratorService = new ModelSeederGeneratorService(fileManager, templateEngine);
        (new SeederGenerator(factoryGeneratorService, seederGeneratorService, e,fileManager)).generateFactoryAndSeeder(Constants.MODEL, modelName, migrationContents);
    }


    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isEnabled = modelParser.isLaravelModel(file) || modelParser.isLaravelMigration(file);
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }


}
