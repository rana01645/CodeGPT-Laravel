package com.trickbd.codegpt.repository.data.local;

public interface DataSetter {
    void set(String key, String value);
    void remove(String key);
}
