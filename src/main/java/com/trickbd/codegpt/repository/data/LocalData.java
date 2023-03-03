package com.trickbd.codegpt.repository.data;

import com.intellij.ide.util.PropertiesComponent;

public class LocalData {
    private static final String PLUGIN_PREFIX = "CodeGPT.";
    private static final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    public static String get(String key) {
        return propertiesComponent.getValue(PLUGIN_PREFIX + key);
    }

    public static void set(String key, String value) {
        propertiesComponent.setValue(PLUGIN_PREFIX + key, value);
    }

    public static void remove(String key) {
        propertiesComponent.unsetValue(PLUGIN_PREFIX + key);
    }
}

