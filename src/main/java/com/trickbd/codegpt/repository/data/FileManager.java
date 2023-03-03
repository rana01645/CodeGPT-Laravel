package com.trickbd.codegpt.repository.data;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileManager {

    public static VirtualFile saveFile(String directory, String filename, String content, Project project) throws IOException {
        // Get the base path of the project
        String basePath = project.getBasePath();

        // Create the directory if it doesn't exist
        VirtualFile dir = VfsUtil.createDirectories(basePath + "/" + directory);

        // Create the file
        VirtualFile file = dir.findOrCreateChildData(null, filename);

        // Write the contents of the file
        try {
            OutputStream outputStream = file.getOutputStream(null);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public String readFile(VirtualFile file) {
        // Get a reference to the current file
        if (file == null) {
            return null;
        }

        // Read the contents of the file
        String contents;
        try {
            contents = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
            return contents;
        } catch (IOException ex) {
            return null;
        }
    }
}

