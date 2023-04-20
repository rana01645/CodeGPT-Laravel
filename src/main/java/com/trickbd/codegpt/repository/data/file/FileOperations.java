package com.trickbd.codegpt.repository.data.file;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public interface FileOperations {
    VirtualFile saveFile(String directory, String filename, String content, Project project) throws IOException;
    String readFile(VirtualFile file);
}
