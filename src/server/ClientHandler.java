package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String nickname;

    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //authentication cycle
                    while (true) {
                        String message = inputStream.readUTF();

                        if (message.startsWith("/auth")) {
                            String[] token = message.split("\\s");

                            String newNickname = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);

                            if (newNickname != null) {
                                nickname = newNickname;
                                sendMessage("/authok " + nickname);
                                server.subscribe(this);
                                System.out.println("The client " + nickname + " has been connected.");
                                break;
                            } else {
                                sendMessage("Invalid login or password.");
                            }
                        }
                    }

                    // work cycle
                    while (true) {
                        String message = inputStream.readUTF();

                        if (message.equals("/end")) {
                            break;
                        }

                        server.broadcastMessage(this, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("The client has been disconnected");
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
