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
import com.trickbd.codegpt.repository.data.local.LocalData;
import com.trickbd.codegpt.services.*;
import com.trickbd.codegpt.settings.SettingsPanel;

import static com.trickbd.codegpt.helper.ModelParser.isLaravelMigration;
import static com.trickbd.codegpt.helper.ModelParser.isLaravelModel;

public class GenerateSeederAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get a reference to the current file
        VirtualFile modelFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (modelFile == null) {
            return;
        }

        Project project = e.getProject();
        if (project == null) {
            return;
        }


        // Check if the file is a Laravel model
        boolean isModel = isLaravelModel(modelFile);
        boolean isMigration = isLaravelMigration(modelFile);
        if (!isModel && !isMigration) {
            return;
        }

        String modelName;
        String migrationContents;

        if (isModel) {
            VirtualFile migrationFile;
            modelName = ModelParser.parseModelName(FileManager.getInstance().readFile(modelFile));
            migrationFile = (new ModelFinder()).findMigrationFileForModel(modelName, project, new CaseChanger());
            if (migrationFile == null) {
                return;
            }

            // Read the contents of the model and migration files
            migrationContents = FileManager.getInstance().readFile(migrationFile);
        } else {
            // Read the contents of the model and migration files
            modelName = (new ModelFinder()).findModelNameForMigration(modelFile, new CaseChanger());
            migrationContents = FileManager.getInstance().readFile(modelFile);
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

        FileManager fileManager = FileManager.getInstance();
        TemplateEngine templateEngine = new MustacheTemplateEngine();
        SeederGeneratorService seederGeneratorService = new ModelSeederGeneratorService(fileManager, templateEngine);
        (new SeederGenerator(factoryGeneratorService, seederGeneratorService, e)).generateFactoryAndSeeder(Constants.MODEL, modelName, migrationContents);
    }


    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isEnabled = isLaravelModel(file) || isLaravelMigration(file);
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }


}
