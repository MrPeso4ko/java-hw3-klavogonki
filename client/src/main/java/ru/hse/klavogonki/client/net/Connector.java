package ru.hse.klavogonki.client.net;

import javafx.application.Platform;
import ru.hse.klavogonki.client.MainMenuController;

import java.io.IOException;
import java.net.Socket;

public class Connector implements Runnable {

    private final String host;
    private final int port;
    private final ClientSideConnection connection;

    private final MainMenuController controller;

    public Connector(String host, int port, ClientSideConnection connection, MainMenuController controller) {
        this.host = host;
        this.port = port;
        this.connection = connection;
        this.controller = controller;
    }

    public void run() {
        try {
            Socket socket = new Socket(host, port);
            connection.setSocket(socket);
            Platform.runLater(controller::Connected);
        } catch (IOException e) {
            Platform.runLater(controller::ConnectionError);
        }
    }
}
