package com.trickbd.codegpt.services;

import com.trickbd.codegpt.engines.TemplateEngine;
import com.trickbd.codegpt.repository.data.file.FileManager;

import java.util.HashMap;
import java.util.Map;

public class ModelSeederGeneratorService implements SeederGeneratorService {
    private final FileManager fileManager;
    private final TemplateEngine templateEngine;

    public ModelSeederGeneratorService(FileManager fileManager, TemplateEngine templateEngine) {
        this.fileManager = fileManager;
        this.templateEngine = templateEngine;
    }

    @Override
    public String generateSeeder(String modelName) {

        String templateContent = fileManager.readResource("/template/ModelSeeder.mustache");
        Map<String, Object> variables = new HashMap<>();
        variables.put("modelName", modelName);
        return templateEngine.render(templateContent, variables);
    }
}
