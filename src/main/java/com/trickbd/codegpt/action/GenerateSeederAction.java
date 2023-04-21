package com.trickbd.codegpt.action;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.constants.Constants;
import com.trickbd.codegpt.generator.SeederGenerator;
import com.trickbd.codegpt.helper.ModelParser;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.repository.data.local.LocalData;
import com.trickbd.codegpt.settings.SettingsPanel;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        if (isModel){
             VirtualFile migrationFile;
            modelName = ModelParser.parseModelName(FileManager.getInstance().readFile(modelFile));
            migrationFile = findMigrationFileForModel(modelName, project);
            if (migrationFile == null) {
                return;
            }

            // Read the contents of the model and migration files
            migrationContents = FileManager.getInstance().readFile(migrationFile);
        }else {
            // Read the contents of the model and migration files
            modelName = findModelNameForMigration(modelFile);
            migrationContents = FileManager.getInstance().readFile(modelFile);
        }



        // Generate the seeder
        String apiKey = LocalData.getInstance(PropertiesComponent.getInstance()).get(Constants.API_KEY);
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new SeederGenerator(apiKey1, modelName, migrationContents, modelFile, e)).generateSeeder();
                }
            });
            settingsPanel.show();
            return;
        }

        (new SeederGenerator(apiKey, modelName, migrationContents, modelFile, e)).generateSeeder();
    }

    private String findModelNameForMigration(VirtualFile modelFile) {
        String contents = FileManager.getInstance().readFile(modelFile);
        String regex = "Schema::create\\('(\\w+)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contents);
        if (!matcher.find()) {
            return null;
        }
        String tableName = matcher.group(1);
        String modelName =  toPascalCase(tableName);
        //remove the s from the end of the model name
        modelName = modelName.substring(0, modelName.length() - 1);
        return modelName;
    }

    private static String toPascalCase(String str) {
        return Arrays.stream(str.split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }

    private VirtualFile findMigrationFileForModel(String modelName, Project project) {
        // Find the corresponding migration file
        String migrationFileName = String.format("create_%ss_table.php", toSnakeCase(modelName));
        System.out.println("Migration file name: " + migrationFileName);
        VirtualFile migrationsDir = project.getBaseDir().findFileByRelativePath("database/migrations");
        if (migrationsDir == null) {
            return null;
        }
        VirtualFile[] migrationFiles = migrationsDir.getChildren();
        System.out.println("Migration files found: " + migrationFiles.length);
        VirtualFile migrationFile = null;
        for (VirtualFile file : migrationFiles) {
            if (file.getName().toLowerCase().contains(migrationFileName.toLowerCase())) {
                System.out.println("Migration file found: " + file.getPath());
                return file;
            }
        }
        System.out.println("Migration file not found.");
        return null;
    }

    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }


    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isEnabled = isLaravelModel(file) || isLaravelMigration(file);
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }


}
