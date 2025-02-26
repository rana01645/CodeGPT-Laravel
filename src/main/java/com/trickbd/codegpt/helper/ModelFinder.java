package com.trickbd.codegpt.helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.file.FileManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelFinder {

    FileManager fileManager;

    public ModelFinder(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Finds the model name from a migration file by extracting the table name.
     */
    public String findModelNameForMigration(VirtualFile modelFile, CaseChanger caseChanger) {
        String contents = fileManager.readFile(modelFile);
        System.out.println("Reading migration file: " + contents);
        String regex = "Schema::create\\('(\\w+)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contents);
        if (!matcher.find()) {
            return null;
        }

        String tableName = matcher.group(1);
        String modelName = caseChanger.toPascalCase(tableName);

        // Remove 's' at the end only if it is plural
        return singularize(modelName);
    }

    /**
     * Finds the corresponding migration file for a given model.
     */
    public VirtualFile findMigrationFileForModel(String modelName, Project project, CaseChanger caseChanger) {
        if (project == null || project.getBaseDir() == null) {
            System.out.println("Invalid project directory.");
            return null;
        }

        VirtualFile migrationsDir = project.getBaseDir().findFileByRelativePath("database/migrations");
        if (migrationsDir == null) {
            System.out.println("Migrations directory not found.");
            return null;
        }

        String tableName = caseChanger.toSnakeCase(modelName);
        String pluralTableName = pluralize(tableName);
        System.out.println("Searching for migration file for model: " + modelName);

        List<String> migrationPatterns = List.of(
                String.format("create_%s_table", tableName),
                String.format("create_%s_table", pluralTableName)
        );

        System.out.println("Expected migration file names: " + migrationPatterns);


        for (VirtualFile file : migrationsDir.getChildren()) {
            String fileName = file.getName().toLowerCase();
            for (String pattern : migrationPatterns) {
                System.out.println("Checking pattern: " + pattern);
                System.out.println("File name: " + fileName);
                if (matchesMigrationPattern(fileName, pattern)) {
                    System.out.println("Migration file found: " + file.getPath());
                    return file;
                }
            }
        }

        System.out.println("Migration file not found.");
        return null;
    }

    /**
     * Converts a singular noun to plural form using basic English rules.
     */
    String pluralize(String singular) {
        if (singular.endsWith("y") && !singular.matches(".*[aeiou]y$")) {
            return singular.substring(0, singular.length() - 1) + "ies";
        } else if (singular.matches(".*(s|x|z|sh|ch)$")) {
            return singular + "es";
        } else {
            return singular + "s";
        }
    }

    /**
     * Converts a plural model name back to singular.
     */
    String singularize(String plural) {
        if (plural.endsWith("ies")) {
            return plural.substring(0, plural.length() - 3) + "y";
        } else if (plural.matches(".*(s|x|z|sh|ch)es$")) {
            return plural.substring(0, plural.length() - 2);
        } else if (plural.endsWith("s") && !plural.endsWith("ss")) {
            return plural.substring(0, plural.length() - 1);
        }
        return plural;
    }

    /**
     * Validates if a migration file matches Laravel's naming pattern.
     */
    boolean matchesMigrationPattern(String fileName, String migrationName) {
        String regex = ".*\\d{4}_\\d{2}_\\d{2}_\\d{6}_" + migrationName + "\\.php$";
        return fileName.matches(regex);
    }
}
