package webserver;

import http.HttpHeaders;
import http.StatusCode;
import model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import utils.FileLoader;
import utils.HtmlEditor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum GetType implements MethodType{
    AUTHORIZED_HOME("/index.html"){
        @Override
        public HttpMessage service(HttpMessage request) throws IOException{
            //쿠키로 유효한 세션 아이디를 넘겨주는 경우 index.html에 사용자 이름 표시하고 로그인 버튼 없애기
            Session session = UserService.isValidSession(request);
            Map<String, String> responseInfo = new HashMap<>();
            responseInfo.put(HttpHeaders.HTTP_VERSION, request.getHttpVersion());
            responseInfo.put(HttpHeaders.CONTENT_TYPE, request.getAccept());
            if(session!=null){
                byte[] body = HtmlEditor.includeUserNameOnHomePage(session.getUser().getName());
                responseInfo.put(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length));
                responseInfo.put(HttpHeaders.STATUS_CODE, StatusCode.OK);
                return new HttpMessage(responseInfo, body);
            }
            //쿠키 없이 요청이 오거나 유효하지 않은 세션 아이디인 경우 변형 없는 index.html 반환
            else{
                return OTHERS.service(request);
            }
        }
    },
    LOGIN_USER_LIST("/user/list.html"){
        @Override
        public HttpMessage service(HttpMessage request) throws IOException{
            Map<String, String> responseInfo = new HashMap<>();
            responseInfo.put(HttpHeaders.HTTP_VERSION, request.getHttpVersion());
            responseInfo.put(HttpHeaders.CONTENT_TYPE, request.getAccept());
            byte[] body = HtmlEditor.listLoginUser();
            responseInfo.put(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length));
            responseInfo.put(HttpHeaders.STATUS_CODE, StatusCode.OK);
            return new HttpMessage(responseInfo, body);
        }
    },
    OTHERS("*"){
        @Override
        public HttpMessage service(HttpMessage request) throws IOException {
            Map<String, String> responseInfo = new HashMap<>();
            responseInfo.put(HttpHeaders.HTTP_VERSION, request.getHttpVersion());
            responseInfo.put(HttpHeaders.CONTENT_TYPE, request.getAccept());
            byte[] body;
            try{
                body = FileLoader.loadFileContent(request.getPath(), request.getMimeType());
                responseInfo.put(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length));
            } catch (IOException e){    //없는 페이지 요청시 404 에러
                logger.error("Fault Page Request : {}", request.getPath());
                responseInfo.put(HttpHeaders.STATUS_CODE, StatusCode.NOT_FOUND);
                body = FileLoader.loadFileContent("/error/not_found.html","html");
                return new HttpMessage(responseInfo, body);
            }
            responseInfo.put(HttpHeaders.STATUS_CODE, StatusCode.OK);
            return new HttpMessage(responseInfo, body);
        }
    }
    ;

    private static final Logger logger = LoggerFactory.getLogger(GetType.class);
    private final String path;

    GetType(String path){
        this.path=path;
    }

    public static GetType findByPath(String path){
        return Arrays.stream(GetType.values())
                .filter(func -> func.path.equals(path))
                .findAny()
                .orElse(OTHERS);
    }
}
