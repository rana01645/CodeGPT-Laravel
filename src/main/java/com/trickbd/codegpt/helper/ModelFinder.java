package com.trickbd.codegpt.helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.file.FileManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelFinder {

    public String findModelNameForMigration(VirtualFile modelFile, CaseChanger caseChanger){
        String contents = FileManager.getInstance().readFile(modelFile);
        String regex = "Schema::create\\('(\\w+)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contents);
        if (!matcher.find()) {
            return null;
        }
        String tableName = matcher.group(1);
        String modelName = caseChanger.toPascalCase(tableName);
        //remove the s from the end of the model name
        modelName = modelName.substring(0, modelName.length() - 1);
        return modelName;
    }

    public VirtualFile findMigrationFileForModel(String modelName, Project project, CaseChanger caseChanger) {
        // Find the corresponding migration file
        String migrationFileName = String.format("create_%ss_table.php", caseChanger.toSnakeCase(modelName));
        System.out.println("Migration file name: " + migrationFileName);
        VirtualFile migrationsDir = project.getBaseDir().findFileByRelativePath("database/migrations");
        if (migrationsDir == null) {
            return null;
        }
        VirtualFile[] migrationFiles = migrationsDir.getChildren();
        System.out.println("Migration files found: " + migrationFiles.length);
        for (VirtualFile file : migrationFiles) {
            if (file.getName().toLowerCase().contains(migrationFileName.toLowerCase())) {
                System.out.println("Migration file found: " + file.getPath());
                return file;
            }
        }
        System.out.println("Migration file not found.");
        return null;
    }

}

