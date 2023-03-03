package com.trickbd.codegpt.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestParser {

    public static String parseDirectory(String phpCode) {
        Pattern pattern = Pattern.compile("namespace\\s+([\\w\\\\]+)\\s*;");
        Matcher matcher = pattern.matcher(phpCode);
        if (matcher.find()) {
            String namespace = matcher.group(1);
            String[] parts = namespace.split("\\\\");
            StringBuilder path = new StringBuilder("tests/");
            for (int i = 1; i < parts.length; i++) {
                if (i == parts.length - 1) {
                    path.append(parts[i]);
                } else {
                    path.append(parts[i]).append("/");
                }

            }
            return path.toString();
        } else {
            return "tests/Unit";
        }
    }

    public static String parseFilename(String phpCode, String filename) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s+extends");
        Matcher matcher = pattern.matcher(phpCode);

        if (matcher.find()) {
            String classname = matcher.group(1);
            return classname+".php"; //
        } else {
            return TestParser.getTestFileName(filename);
        }
    }


    public static String getTestFileName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        String baseName = fileName.substring(0, lastDotIndex);
        String extension = fileName.substring(lastDotIndex);

        return baseName + "Test" + extension;
    }


}
