package com.trickbd.codegpt.repository.data.file;

/**
 * Factory class to provide instances of FileManager.
 */
public class FileManagerFactory {
    private static FileManager instance;

    public static FileManager createDefault() {
        if (instance == null) {
            instance = new FileManager(new FileWriter(), new FileReader(), new ResourceFileReader());
        }
        return instance;
    }
}
