package com.trickbd.codegpt.repository.data.file;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileReader {
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
