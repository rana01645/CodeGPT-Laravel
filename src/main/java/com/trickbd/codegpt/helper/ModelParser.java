package com.trickbd.codegpt.helper;

import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.file.FileManager;

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

}

