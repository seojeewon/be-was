package db;

import com.google.common.collect.Maps;

import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static Map<String, User> users = Maps.newHashMap();
    private static Map<String, Session> sessions = Maps.newHashMap();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAllUser() {
        return users.values();
    }

    public static void addSession(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public static Session findSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    public static List<Session> findAllValidSession() {
        return sessions.values().stream()
                .filter(session -> session.getExpireDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

}
