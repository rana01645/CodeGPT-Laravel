package com.trickbd.codegpt.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.generator.SeederGenerator;
import com.trickbd.codegpt.helper.ModelParser;
import com.trickbd.codegpt.repository.data.FileManager;
import com.trickbd.codegpt.repository.data.LocalData;
import com.trickbd.codegpt.settings.SettingsPanel;

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
        if (!isLaravelModel(modelFile)) {
            return;
        }

        // Find the corresponding migration file
        String modelName = ModelParser.parseModelName((new FileManager()).readFile(modelFile));
        String migrationFileName = String.format("create_%ss_table.php", toSnakeCase(modelName));
        System.out.println("Migration file name: " + migrationFileName);
        VirtualFile migrationsDir = project.getBaseDir().findFileByRelativePath("database/migrations");
        if (migrationsDir == null) {
            return;
        }
        VirtualFile[] migrationFiles = migrationsDir.getChildren();
        System.out.println("Migration files found: " + migrationFiles.length);
        VirtualFile migrationFile = null;
        for (VirtualFile file : migrationFiles) {
            if (file.getName().toLowerCase().contains(migrationFileName.toLowerCase())) {
                System.out.println("Migration file found: " + file.getPath());
                migrationFile = file;
                break;
            }
        }
        if (migrationFile == null) {
            System.out.println("Migration file not found.");
            return;
        }

        // Read the contents of the model and migration files
        String modelContents = (new FileManager()).readFile(modelFile);
        if (modelContents == null) {
            return;
        }
        String migrationContents = (new FileManager()).readFile(migrationFile);
        if (migrationContents == null) {
            return;
        }

        // Generate the seeder
        String apiKey = LocalData.get("apiKey");
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new SeederGenerator(apiKey1, modelContents, migrationContents, modelFile, e)).generateSeeder();
                }
            });
            settingsPanel.show();
            return;
        }

        (new SeederGenerator(apiKey, modelContents, migrationContents, modelFile, e)).generateSeeder();
    }

    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }


    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isEnabled = isLaravelModel(file);
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }


}
