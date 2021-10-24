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

    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {

                            String message = inputStream.readUTF();

                            if (message.equals("/end")) {
                                System.out.println("The client has been disconnected");
                                break;
                            }

                            server.broadcastMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("The client has been disconnected");
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
}
