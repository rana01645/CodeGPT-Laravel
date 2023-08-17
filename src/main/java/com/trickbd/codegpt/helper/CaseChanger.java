package com.trickbd.codegpt.helper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CaseChanger {

    public String toPascalCase(String str) {
        return Arrays.stream(str.split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }


    public String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
