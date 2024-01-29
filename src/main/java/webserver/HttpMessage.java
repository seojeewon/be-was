package webserver;

import dto.LoginInfo;
import http.constants.HttpHeaders;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

public class HttpMessage {
    private static final Logger logger = LoggerFactory.getLogger(HttpMessage.class);
    Map<String, String> httpMessage;
    byte[] body;

    public HttpMessage(Map<String, String> httpMessage) {
        this.httpMessage = httpMessage;
    }

    public HttpMessage(Map<String, String> httpMessage, byte[] body) {
        this.httpMessage = httpMessage;
        this.body = body;
    }

    public String getRequestInfo() {
        return "[Method: " + getMethod() + ", "
                + "Path: " + getPath() + ", "
                + "Accept: " + getAccept() + "]";
    }

    public String getMethod() {
        return httpMessage.get(HttpHeaders.METHOD);
    }

    public String getAccept() {
        String[] acceptList = httpMessage.get(HttpHeaders.ACCEPT).split(",");
        return acceptList[0];
    }

    public String getPath() {
        return httpMessage.get(HttpHeaders.PATH);
    }

    public String getMimeType() {
        String[] types = getPath().split("\\.");
        return types[types.length-1];
    }

    public byte[] getResponseBody() {
        return body;
    }

    public String getRequestBody(){
        return httpMessage.get("Body");
    }

    public String getSetCookie(){
        return httpMessage.get(HttpHeaders.SET_COOKIE);
    }

    public LoginInfo getLoginDto(){
        String[] loginInfo = httpMessage.get("Body").split("&");
        String[] userIdPart = loginInfo[0].split("=");
        String[] pwPart = loginInfo[1].split("=");
        return new LoginInfo(userIdPart[1], pwPart[1]);
    }

    public String getHttpVersion() {
        return httpMessage.get(HttpHeaders.HTTP_VERSION);
    }

    public String getStatusCode(){
        return httpMessage.get(HttpHeaders.STATUS_CODE);
    }

    public String getLocation(){
        return httpMessage.get(HttpHeaders.LOCATION);
    }

    public String getContentType(){
        return httpMessage.get(HttpHeaders.CONTENT_TYPE);
    }

    public String getCookie(){
        String cookie = httpMessage.get(HttpHeaders.COOKIE);
        if(cookie==null) return null;
        String[] cookiePart = cookie.split("=");
        return cookiePart[1];
    }

    public String getResponseInfo() {
        return "[Status: " + httpMessage.get(HttpHeaders.STATUS_CODE) + ", "
                + "Content-Type: " + getContentType() + "]";
    }

    public User getUserInfo() {
        String[] userInfo = httpMessage.get("Body").split("&");
        Class<User> userClazz = User.class;
        User user = new User();
        for(String info : userInfo){
            String[] pieces = info.split("=");
            try{
                Field field = userClazz.getDeclaredField(pieces[0]);
                field.setAccessible(true);
                field.set(user, pieces[1]);
            } catch (NoSuchFieldException | IllegalAccessException e){
                logger.error("존재하지 않는 유저 필드입니다 : {}", e.getMessage());
            }

        }
        return user;
    }

    public String getHeader(String headerName){
        return httpMessage.get(headerName);
    }
}
