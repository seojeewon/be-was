package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import utils.HttpRequestParser;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final UserService userService = new UserService();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequestParser parser = new HttpRequestParser();
            HttpRequest httpRequest = parser.parseRequestInputStream(in);
            logger.debug(httpRequest.toString());

            HttpMessage responseMessage = RequestMethodType.valueOf(httpRequest.getMessage().getMethod()).operator(httpRequest);
            HttpResponse response = new HttpResponse(responseMessage);
            logger.debug(response.toString());
//            dos.writeBytes("HTTP1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: "+responseMessage.getAccept()+" \r\n");
//            dos.writeBytes("Content-Length: " + responseMessage.getResponseBody().length + "\r\n\r\n");
//            dos.write(responseMessage.body);
//            dos.flush();
            ResponseStatusType.findByStatus(responseMessage.getStatusCode()).writer(dos, response);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
