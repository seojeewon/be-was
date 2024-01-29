package webserver;

import http.HttpHeaders;
import http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public enum ResponseStatusType {
    OK(StatusCode.OK) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            HttpMessage message = response.getMessage();
            dos.writeBytes(message.getHttpVersion() + " " + message.getStatusCode() + " \r\n");
            dos.writeBytes(HttpHeaders.CONTENT_TYPE + ": " + message.getContentType() + ";charset=utf-8 \r\n");
            dos.writeBytes(HttpHeaders.CONTENT_LENGTH + ": " + message.getResponseBody().length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(message.body, 0, message.body.length);
            dos.flush();
        }
    },
    FOUND(StatusCode.FOUND) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            HttpMessage message = response.getMessage();
            dos.writeBytes(message.getHttpVersion() + " " + message.getStatusCode() + " \r\n");
            dos.writeBytes(HttpHeaders.LOCATION + ": " + message.getLocation() + " \r\n");
            if (message.getSetCookie() != null) {
                dos.writeBytes(HttpHeaders.SET_COOKIE + ": sid=" + message.getSetCookie() + "; Path=/ \r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        }
    },
    INTERNAL_SERVER_ERROR(StatusCode.INTERNAL_SERVER_ERROR) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            ResponseStatusType.errorMessageMaker(dos, response.getMessage());
        }
    },
    NOT_FOUND(StatusCode.NOT_FOUND) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            ResponseStatusType.errorMessageMaker(dos, response.getMessage());
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(ResponseStatusType.class);

    private final String statusCode;

    ResponseStatusType(String statusCode) {
        this.statusCode = statusCode;
    }

    abstract void writer(DataOutputStream dos, HttpResponse response) throws IOException;

    public static ResponseStatusType findByStatus(String statusCode) {
        return Arrays.stream(ResponseStatusType.values())
                .filter(code -> code.statusCode.equals(statusCode))
                .findAny()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    private static void errorMessageMaker(DataOutputStream dos, HttpMessage message) throws IOException {
        dos.writeBytes(message.getHttpVersion() + " " + message.getStatusCode() + " \r\n");
        dos.writeBytes(HttpHeaders.CONTENT_TYPE + ": text/html;charset=utf-8");
        dos.writeBytes("\r\n");
        dos.write(message.body, 0, message.body.length);
        dos.flush();
    }
}
