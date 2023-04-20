package com.trickbd.codegpt.repository.data.local;

import com.intellij.ide.util.PropertiesComponent;

public class LocalData implements DataGetter, DataSetter {
    private static LocalData instance;
    private static final String PLUGIN_PREFIX = "CodeGPT.";
    private final PropertiesComponent propertiesComponent;

    public LocalData(PropertiesComponent propertiesComponent) {
        this.propertiesComponent = propertiesComponent;
    }

    public static LocalData getInstance(PropertiesComponent propertiesComponent) {
        if (instance == null) {
            instance = new LocalData(propertiesComponent);
        }
        return instance;
    }

    @Override
    public String get(String key) {
        return propertiesComponent.getValue(PLUGIN_PREFIX + key);
    }

    @Override
    public void set(String key, String value) {
        propertiesComponent.setValue(PLUGIN_PREFIX + key, value);
    }

    @Override
    public void remove(String key) {
        propertiesComponent.unsetValue(PLUGIN_PREFIX + key);
    }
}
