package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    List<ClientHandler> clients;

    private static int PORT = 8189;
    Socket socket = null;

    public Server() {
        clients = new Vector<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server has been started.");

            while (true) {
                socket = serverSocket.accept();
                System.out.println("The client has been connected.");
                clients.add(new ClientHandler(this, socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }
}
