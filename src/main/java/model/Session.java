package model;

import java.time.LocalDateTime;

public class Session {
    private String sessionId;
    private User user;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
    private LocalDateTime lastAccessDate;

    public Session(String sessionId, User user, LocalDateTime createDate){
        this.sessionId=sessionId;
        this.user=user;
        this.createDate=createDate;
        this.expireDate=this.createDate.plusSeconds(15);
    }

    public String getSessionId(){
        return this.sessionId;
    }

    public LocalDateTime getExpireDate(){
        return this.expireDate;
    }

    public User getUser(){
        return this.user;
    }

}
