package webserver;

import http.constants.HttpHeaders;
import http.constants.Path;
import http.constants.StatusCode;
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
                return get500ErrorMessage(request);
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
                return get500ErrorMessage(request);
            }
        }
    }
    ;

    private static HttpMessage get500ErrorMessage(HttpMessage request) throws IOException {
        Map<String, String> responseInfo = new HashMap<>();
        responseInfo.put(HttpHeaders.HTTP_VERSION, request.getHttpVersion());
        responseInfo.put(HttpHeaders.CONTENT_TYPE, request.getContentType());
        responseInfo.put(HttpHeaders.STATUS_CODE, StatusCode.INTERNAL_SERVER_ERROR);
        byte[] body = FileLoader.loadFileContent(Path.ERROR_500,"html");
        return new HttpMessage(responseInfo, body);
    }


    abstract HttpMessage operator(HttpRequest httpRequest) throws IOException;

}
