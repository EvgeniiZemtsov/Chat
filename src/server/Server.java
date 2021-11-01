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
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoggedIn(String login) {
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clientsList ");
        for (ClientHandler client : clients) {
            sb.append(client.getNickname()).append(" ");
        }
        String message = sb.toString();
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}
