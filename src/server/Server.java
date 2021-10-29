package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    private static int PORT = 8189;
    private Socket socket = null;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server has been started.");

            while (true) {
                socket = serverSocket.accept();
//                System.out.println("The client has been connected.");
//                subscribe(new ClientHandler(this, socket));
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(ClientHandler sender, String message) {
        String messageToSend = String.format("%s : %s", sender.getNickname(), message);

        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(messageToSend);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
