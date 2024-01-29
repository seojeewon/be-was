package utils;

import http.constants.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpMessage;
import webserver.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;


public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parseRequestInputStream(InputStream in) throws IOException {
        Map<String, String> messageInfo = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        //첫 번째 라인에서 메서드, 경로 추출
        String firstLine = reader.readLine();
        String[] firstLineParts = firstLine.split(" ");
        messageInfo.put(HttpHeaders.METHOD, firstLineParts[0]);
        messageInfo.put(HttpHeaders.PATH, firstLineParts[1]);
        messageInfo.put(HttpHeaders.HTTP_VERSION, firstLineParts[2]);

        //다른 헤더 추출
        String line;
        while((line=reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                messageInfo.put(headerParts[0], headerParts[1]);
            }
        }

        // 바디 추출
        if(messageInfo.get(HttpHeaders.METHOD).equals("POST")){
            char[] body = new char[Integer.parseInt(messageInfo.get(HttpHeaders.CONTENT_LENGTH))];
            reader.read(body);
            messageInfo.put("Body", new String(body));
        }
        HttpMessage message = new HttpMessage(messageInfo);
        return new HttpRequest(message);
    }


}
