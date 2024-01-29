package utils;

import http.constants.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileLoader {
    private static final Map<String, String> TYPE_PATH = new HashMap<>();

    static {
        TYPE_PATH.put("css", Path.STATIC_PATH);
        TYPE_PATH.put("eot", Path.STATIC_PATH);
        TYPE_PATH.put("ttf", Path.STATIC_PATH);
        TYPE_PATH.put("woff", Path.STATIC_PATH);
        TYPE_PATH.put("woff2", Path.STATIC_PATH);
        TYPE_PATH.put("svg", Path.STATIC_PATH);
        TYPE_PATH.put("png", Path.STATIC_PATH);
        TYPE_PATH.put("js", Path.STATIC_PATH);
        TYPE_PATH.put("html", Path.TEMPLATES_PATH);
        TYPE_PATH.put("ico", Path.STATIC_PATH);
    }

    public static byte[] loadFileContent(String requestPath, String mimeType) throws IOException {
        return Files.readAllBytes(new File(TYPE_PATH.get(mimeType) + requestPath).toPath());
    }


}
