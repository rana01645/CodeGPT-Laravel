package com.trickbd.codegpt.helper;

import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.repository.data.file.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ModelFinderTest {
    private ModelFinder modelFinder;
    private CaseChanger caseChanger;
    private Project project;
    private VirtualFile migrationsDir;
    private FileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = mock(FileManager.class);
        modelFinder = new ModelFinder(fileManager);
        caseChanger = mock(CaseChanger.class);
        project = mock(Project.class);
        migrationsDir = mock(VirtualFile.class);

        when(project.getBaseDir()).thenReturn(migrationsDir);
    }

    @Test
    void testPluralize() {
        assertEquals("categories", modelFinder.pluralize("category"));
        assertEquals("boxes", modelFinder.pluralize("box"));
        assertEquals("users", modelFinder.pluralize("user"));
        assertEquals("data_queries", modelFinder.pluralize("data_query"));
    }

    @Test
    void testFileNameMatchesPattern() {
        assertTrue(modelFinder.matchesMigrationPattern("2024_02_24_123456_create_users_table.php", "create_users_table"));
    }

    @Test
    void testSingularize() {
        assertEquals("category", modelFinder.singularize("categories"));
        assertEquals("box", modelFinder.singularize("boxes"));
        assertEquals("user", modelFinder.singularize("users"));
        assertEquals("data_query", modelFinder.singularize("data_queries"));
    }

    @Test
    void testFindMigrationFileForModel_Found() {
        VirtualFile migrationFile1 = new MockVirtualFile("2024_02_24_123456_create_users_table.php");
        VirtualFile migrationFile2 = new MockVirtualFile("2024_02_24_654321_create_data_queries_table.php");

        when(caseChanger.toSnakeCase("User")).thenReturn("user");
        when(migrationsDir.findFileByRelativePath("database/migrations")).thenReturn(migrationsDir);
        when(migrationsDir.getChildren()).thenReturn(new VirtualFile[]{migrationFile1, migrationFile2});

        System.out.println(Arrays.toString(migrationsDir.getChildren()));

        VirtualFile foundFile = modelFinder.findMigrationFileForModel("User", project, caseChanger);
        assertNotNull(foundFile);
        assertEquals("2024_02_24_123456_create_users_table.php", foundFile.getName());
    }

    @Test
    void testFindMigrationFileForModel_NotFound() {
        when(caseChanger.toSnakeCase("NonExistentModel")).thenReturn("non_existent_model");
        when(migrationsDir.findFileByRelativePath("database/migrations")).thenReturn(migrationsDir);
        when(migrationsDir.getChildren()).thenReturn(new VirtualFile[]{});

        VirtualFile foundFile = modelFinder.findMigrationFileForModel("NonExistentModel", project, caseChanger);
        assertNull(foundFile);
    }

    @Test
    void testFindModelNameForMigration() {
        VirtualFile modelFile = mock(VirtualFile.class);
        when(modelFile.getName()).thenReturn("User.php");
        when(fileManager.readFile(modelFile))
                .thenReturn("Schema::create('users', function (Blueprint $table) {");

        when(caseChanger.toPascalCase("users")).thenReturn("Users");

        String modelName = modelFinder.findModelNameForMigration(modelFile, caseChanger);
        assertEquals("User", modelName);
    }
}

