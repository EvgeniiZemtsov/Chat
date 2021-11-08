package server;

public interface AuthService {

    /**
     * @return nickname if user exists
     * @return null if user doesn't exist
     */
    String getNicknameByLoginAndPassword(String login, String password);

    boolean registration(String login, String password, String nickname);
}
