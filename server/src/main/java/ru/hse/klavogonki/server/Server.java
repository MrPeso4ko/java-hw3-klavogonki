package ru.hse.klavogonki.server;

import ru.hse.klavogonki.models.PlayerInfo;
import ru.hse.klavogonki.server.game.NewConnectionsHandler;
import ru.hse.klavogonki.server.net.Player;
import ru.hse.klavogonki.server.net.ServerSideConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    public static final BlockingQueue<Player> queue = new LinkedBlockingQueue<>();
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        int port;
        if (args.length != 1) {
            throw new IllegalArgumentException("One argument: TCP port should be provided");
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("One argument: TCP port should be provided: " + e);
            }
        }
        Thread playerHandlerThread = new Thread(new NewConnectionsHandler(queue), "PlayerHandlerThread");
        playerHandlerThread.setDaemon(true);
        playerHandlerThread.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    ServerSideConnection connection = new ServerSideConnection(clientSocket);
                    PlayerInfo playerInfo = (PlayerInfo) connection.getInputStream().readObject();
                    queue.add(new Player(playerInfo, connection));
                    System.out.println("New connection from: " + clientSocket.getInetAddress().toString());
                    clients.add(clientSocket);
                } catch (IOException e) {
                    System.out.println("Client disconnected");
                }
            }
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            for (Socket client : clients) {
                try {
                    if (!client.isClosed()) {
                        client.close();
                    }
                } catch (IOException e) {
                    System.out.println("Can't close socket" + client.getInetAddress().toString() + ":" + e);
                }
            }
        }
    }
}
