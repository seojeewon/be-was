package service;

import db.Database;
import dto.LoginInfo;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpMessage;
import webserver.RequestHandler;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public static boolean registerUser(User newUser){
        //이미 존재하는 id일 경우
        if(isJoinedUser(newUser)){
            return false;
        }
        //Database에 새로운 유저 저장
        Database.addUser(newUser);
        return true;
    }

    public static boolean isValidUser(String body){
        String[] info = body.split("&");
        String[] idPart = info[0].split("=");
        String[] pwPart = info[1].split("=");
        User target = Database.findUserById(idPart[1]);
        return target != null && target.getPassword().equals(pwPart[1]);
    }

    public static Session login(LoginInfo loginInfo){
        //Session Id 만들기
        String sessionId = UUID.randomUUID().toString();
        Session newSession = new Session(sessionId, Database.findUserById(loginInfo.getUserId()), LocalDateTime.now());
        Database.addSession(newSession);
        return newSession;
    }

    public static Session isValidSession(HttpMessage request){
        //쿠키가 없는 응답인 경우 false 리턴
        if(request.getCookie()==null){
            return null;
        }
        Session session = Database.findSessionById(request.getCookie());
        //db에 존재하지 않는 세션이거나 만료된 세션일 경우 false 반환
        if(session != null && !session.getExpireDate().isBefore(LocalDateTime.now())){
            return session;
        }
        return null;
    }

    public String findUserName(String userId){
        return Database.findUserById(userId).getName();
    }

    private static boolean isJoinedUser(User user){
        return Database.findUserById(user.getUserId()) != null;
    }
}
