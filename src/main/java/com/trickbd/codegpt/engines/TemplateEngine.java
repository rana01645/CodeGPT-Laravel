package com.trickbd.codegpt.engines;

import java.util.Map;

public interface TemplateEngine {
    String render(String templateContent, Map<String, Object> variables);
}
