package webserver;

import http.HttpHeaders;
import http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public enum ResponseStatusCode {
    OK(StatusCode.OK) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            HttpMessage message = response.getMessage();
            StringBuilder responseMessage = new StringBuilder();
            responseMessage.append(message.getHttpVersion()).append(" ").append(message.getStatusCode()).append(" \r\n");
            responseMessage.append(HttpHeaders.CONTENT_TYPE).append(": ").append(message.getAccept()).append(";charset=utf-8").append(" \r\n");
            responseMessage.append(HttpHeaders.CONTENT_LENGTH).append(": ").append(message.getResponseBody().length).append(" \r\n");
            responseMessage.append("\r\n");
            dos.writeBytes(responseMessage.toString());
            logger.debug("write header");

//            dos.writeBytes(message.getHttpVersion() + " " + message.getStatusCode() + " \r\n");
//            logger.debug("write version, code");
//            dos.writeBytes(HttpHeaders.CONTENT_TYPE + ": " + message.getAccept() + ";charset=utf-8 \r\n");
//            logger.debug("write content-type");
//            dos.writeBytes(HttpHeaders.CONTENT_LENGTH + ": " + message.getResponseBody().length + "\r\n");
//            logger.debug("write content-length");
//            dos.writeBytes("\r\n");
            dos.write(message.body, 0, message.body.length);
            logger.debug("message body length: {}", message.body.length);
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
                dos.writeBytes("Set-Cookie: sid=" + message.getSetCookie() + "; Path=/ \r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        }
    },
    INTERNAL_SERVER_ERROR(StatusCode.INTERNAL_SERVER_ERROR) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            ResponseStatusCode.errorMessageMaker(dos, response.getMessage());
        }
    },
    NOT_FOUND(StatusCode.NOT_FOUND) {
        @Override
        void writer(DataOutputStream dos, HttpResponse response) throws IOException {
            ResponseStatusCode.errorMessageMaker(dos, response.getMessage());
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(ResponseStatusCode.class);

    private final String statusCode;

    ResponseStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    abstract void writer(DataOutputStream dos, HttpResponse response) throws IOException;

    public static ResponseStatusCode findByStatus(String statusCode) {
        return Arrays.stream(ResponseStatusCode.values())
                .filter(code -> code.statusCode.equals(statusCode))
                .findAny()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    private static void errorMessageMaker(DataOutputStream dos, HttpMessage message) throws IOException {
        dos.writeBytes(message.getHttpVersion() + " " + message.getStatusCode() + " \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8");
        dos.writeBytes("\r\n");
        dos.write(message.body, 0, message.body.length);
        dos.flush();
    }
}
