package com.trickbd.codegpt.repository.data.file;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceFileReader {
    public String readFile(String path) {
        // Get a reference to the current file
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            assert inputStream != null;
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
