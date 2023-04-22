package com.trickbd.codegpt.engines;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class MustacheTemplateEngine implements TemplateEngine {
    private final MustacheFactory mustacheFactory;

    public MustacheTemplateEngine() {
        this.mustacheFactory = new DefaultMustacheFactory();
    }

    @Override
    public String render(String templateContent, Map<String, Object> variables) {
        Mustache mustache = mustacheFactory.compile(new StringReader(templateContent), "ModelSeeder");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, variables);
        return writer.toString();
    }
}
