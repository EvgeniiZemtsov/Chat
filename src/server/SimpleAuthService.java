package server;

import java.util.HashMap;
import java.util.Map;

public class SimpleAuthService implements AuthService {

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    Map<String, UserData> users;

    public SimpleAuthService() {
        users = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            UserData user = new UserData("login " + i, "password " + i, "nickname " + i);
            users.put(user.login, user);
        }

        users.put("qwe", new UserData("qwe", "qwe", "qwe"));
        users.put("asd", new UserData("asd", "asd", "asd"));
        users.put("zxc", new UserData("zxc", "zxc", "zxc"));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return users.get(login).nickname;
    }
}
