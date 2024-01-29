package utils;

import db.Database;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlEditor {
    private static final Logger logger = LoggerFactory.getLogger(HtmlEditor.class);
    private static final String LOGIN_BUTTON = "<li><a href=\"user/login.html\" role=\"button\" id=\"loginButton\">로그인</a></li>";
    private static final String LIST_FRAME = "\n<tr>\n" +
            "                    <th scope=\"row\">number</th> <td>userId</td> <td>userName</td> <td>email</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n" +
            "                </tr>\n";

    public static byte[] includeUserNameOnHomePage(String userName) throws IOException {
        byte[] homeHtml = FileLoader.loadFileContent("/index.html", "html");
        String homePage = new String(homeHtml);
        //로그인 버튼 삭제
        homePage = homePage.replace(LOGIN_BUTTON, "");
        homePage = homePage.replace("<span class=\"user-name\"></span>", "<span class=\"user-name\">" + userName + "</span>");
        return homePage.getBytes();
    }

    public static byte[] listLoginUser() throws IOException {
        byte[] listHtml = FileLoader.loadFileContent("/user/list.html", "html");
        String listPage = new String(listHtml);
        List<Session> sessionList = Database.findAllValidSession();
        String userList = makeUserList(sessionList);
        listPage = listPage.replace("<tbody>", "<tbody>" + userList);
        return listPage.getBytes();
    }

    private static String makeUserList(List<Session> sessionList) {
        StringBuilder userList = new StringBuilder();
        logger.debug("session number : {}", sessionList.size());
        for (int number = 1; number < sessionList.size(); number++) {
            String userLine = LIST_FRAME;
            User user = sessionList.get(number-1).getUser();
            userLine = userLine.replace("number", Integer.toString(number));
            userLine = userLine.replace("userId", user.getUserId());
            userLine = userLine.replace("userName", user.getName());
            userLine = userLine.replace("email", user.getEmail());
            userList.append(userLine);
        }
        return userList.toString();
    }
}
