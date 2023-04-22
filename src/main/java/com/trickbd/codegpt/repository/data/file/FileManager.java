package com.trickbd.codegpt.repository.data.file;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class FileManager implements FileOperations {
    private static FileManager instance;
    private final FileWriter fileWriter;
    private final FileReader fileReader;

    private final ResourceFileReader resourceFileReader;

    public FileManager(FileWriter fileWriter, FileReader fileReader, ResourceFileReader resourceFileReader) {
        this.fileWriter = fileWriter;
        this.fileReader = fileReader;
        this.resourceFileReader = resourceFileReader;
    }


    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager(new FileWriter(), new FileReader(), new ResourceFileReader());
        }
        return instance;
    }

    @Override
    public VirtualFile saveFile(String directory, String filename, String content, Project project) throws IOException {
        return fileWriter.saveFile(directory, filename, content, project);
    }

    @Override
    public String readFile(VirtualFile file) {
        return fileReader.readFile(file);
    }

    @Override
    public String readResource(String path) {
        return resourceFileReader.readFile(path);
    }
}
