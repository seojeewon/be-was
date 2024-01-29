package webserver;

import model.Session;
import service.UserService;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

public enum PostType implements MethodType{
    JOIN("/user/create") {  //회원가입
        @Override
        public HttpMessage service(HttpMessage request) {
            Map<String, String> responseInfo = new HashMap<>();
            responseInfo.put("HTTP version", request.getHttpVersion());
            responseInfo.put("Status-Code", "302 Found");
            if(UserService.registerUser(request.getUserInfo())){
                //회원가입에 성공했을 경우 홈페이지로 리다이렉트
                responseInfo.put("Location", "/index.html");
            } else{
                //이미 존재하는 아이디인 경우 form_fail.html로 리다이렉트
                responseInfo.put("Location", "/user/form_fail.html");
            }
            System.out.println("끝");
            return new HttpMessage(responseInfo);
        }
    },
    LOGIN("/user/login") {  //로그인
        @Override
        public HttpMessage service(HttpMessage request) {
            Map<String, String> responseInfo = new HashMap<>();
            responseInfo.put("HTTP version", request.getHttpVersion());
            responseInfo.put("Status-Code", "302 Found");
            if(UserService.isValidUser(request.getRequestBody())){
                Session session = UserService.login(request.getLoginDto());
                //로그인에 성공했을 경우, 세션아이디를 쿠키에 담아 홈페이지로 리다이렉트
                responseInfo.put("Location", "/index.html");
                responseInfo.put("Set-Cookie", session.getSessionId());
            } else{
                //로그인에 실패한 경우 login_failed.html로 리다이렉트
                responseInfo.put("Location", "/user/login_failed.html");
            }
            return new HttpMessage(responseInfo);
        }
    },
    NONE("*"){   //TODO 400번대 에러
        @Override
        public HttpMessage service(HttpMessage requestMessage) {
            return null;
        }
    };

    private final String path;

    PostType(String path) {
        this.path = path;
    }

    public static PostType findByPath(String path){
        return Arrays.stream(PostType.values())
                .filter(func->func.path.equals(path))
                .findAny()
                .orElse(NONE);
    }

}
