package com.trickbd.codegpt.helper;

import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.FileManager;

public class ModelParser {

    public static boolean isLaravelModel(VirtualFile file) {
        if (file == null || file.isDirectory() || !file.getName().endsWith(".php")) {
            return false;
        }

        String contents = FileManager.getInstance().readFile(file);
        if (contents.contains("extends Model")) {
            return true;
        }

        VirtualFile parent = file.getParent();
        return parent != null && parent.getName().equals("Models") && parent.getParent().getName().equals("app");
    }

    public static boolean isLaravelMigration(VirtualFile file) {
        if (file == null || file.isDirectory() || !file.getName().endsWith(".php")) {
            return false;
        }

        String contents = FileManager.getInstance().readFile(file);
        if (!contents.contains("Schema::create") || !contents.contains("Blueprint")) {
            return false;
        }

        VirtualFile parent = file.getParent();
        return parent != null && parent.getName().equals("migrations") && parent.getParent().getName().equals("database");
    }


    public static String parseModelName(String modelContents) {
        // Assume that the model name is defined as the class name
        int startIndex = modelContents.indexOf("class ") + "class ".length();
        int endIndex = modelContents.indexOf(" extends");
        String className = modelContents.substring(startIndex, endIndex).trim();

        // Convert the class name to the model name by removing the "App\\Models\\" namespace prefix
        String modelName = className.replace("App\\Models\\", "");

        return modelName;
    }

    public static String parseTableName(String migrationContents) {
        // Find the "Schema::create" call inside the migration closure
        int startIndex = migrationContents.indexOf("Schema::create");
        int endIndex = migrationContents.indexOf("});", startIndex);
        String createStatement = migrationContents.substring(startIndex, endIndex);

        // Extract the table name argument from the "Schema::create" call
        int tableNameStartIndex = createStatement.indexOf("('") + 2;
        int tableNameEndIndex = createStatement.indexOf("'", tableNameStartIndex);
        String tableName = createStatement.substring(tableNameStartIndex, tableNameEndIndex);

        return tableName;
    }

    public static String parseColumns(String migrationContents) {
        // Find the "Schema::create" call inside the migration closure
        int startIndex = migrationContents.indexOf("Schema::create");
        int endIndex = migrationContents.indexOf("});", startIndex);
        String createStatement = migrationContents.substring(startIndex, endIndex);

        // Extract the columns argument from the "Schema::create" call
        int columnsStartIndex = createStatement.indexOf("function (Blueprint $table) {") + "function (Blueprint $table) {".length();
        int columnsEndIndex = createStatement.indexOf("});", columnsStartIndex) + 2;
        String columnsArray = createStatement.substring(columnsStartIndex, columnsEndIndex);

        return columnsArray;
    }

}

