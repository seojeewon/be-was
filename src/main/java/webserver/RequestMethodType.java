package webserver;

import utils.FileLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum RequestMethodType {
    POST{
        @Override
        HttpMessage operator(HttpRequest httpRequest) throws IOException {
            HttpMessage request = httpRequest.getMessage();
            try{
                return PostType.findByPath(request.getPath()).service(request);
            }
            catch (Exception e){
                System.out.println("Error while POST: " + e.getMessage());
                Map<String, String> responseInfo = new HashMap<>();
                responseInfo.put("HTTP version", request.getHttpVersion());
                responseInfo.put("Accept", request.getAccept());
                responseInfo.put("Status-Code", "500 Internal Server Error");
                byte[] body = FileLoader.loadFileContent("/error/internal_server_error.html","html");
                return new HttpMessage(responseInfo, body);
                 //예외 발생하면 서버에러 코드
            }
        }
    },
    GET{
        @Override
        HttpMessage operator(HttpRequest httpRequest) throws IOException {
            HttpMessage request = httpRequest.getMessage();
            try {
                return GetType.findByPath(request.getPath()).service(request);
            }
            catch (Exception e){
                System.out.println("Error while GET: " + e.getMessage());
                Map<String, String> responseInfo = new HashMap<>();
                responseInfo.put("HTTP version", request.getHttpVersion());
                responseInfo.put("Accept", request.getAccept());
                responseInfo.put("Status-Code", "500 Internal Server Error");
                byte[] body = FileLoader.loadFileContent("/error/internal_server_error.html","html");
                return new HttpMessage(responseInfo, body);
            }
        }
    }
    ;


    abstract HttpMessage operator(HttpRequest httpRequest) throws IOException;

}
