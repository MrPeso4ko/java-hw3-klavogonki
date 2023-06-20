package ru.hse.klavogonki.client.room;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import ru.hse.klavogonki.client.net.ClientSideConnection;
import ru.hse.klavogonki.models.PlayerInfo;
import ru.hse.klavogonki.models.RoomStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class InRoomMessageHandler implements Runnable {
    private final ObservableList<String> listPlayers;
    private final InRoomController controller;
    private final Text timeLeft;
    private final PlayerInfo player;
    private final ClientSideConnection connection;

    public InRoomMessageHandler(ObservableList<String> listPlayers, Text timeLeft, PlayerInfo player, ClientSideConnection connection, InRoomController controller) {
        this.listPlayers = listPlayers;
        this.timeLeft = timeLeft;
        this.player = player;
        this.connection = connection;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream outputStream = connection.getOutputStream();
            outputStream.writeObject(player);
            ObjectInputStream inputStream = connection.getInputStream();
            RoomStatus status;
            while (true) {
                try {
                    status = (RoomStatus) inputStream.readObject();
                    RoomStatus finalStatus = status;
                    Platform.runLater(() -> {
                        listPlayers.clear();
                        listPlayers.addAll(finalStatus.playerNames);
                        timeLeft.setText(String.valueOf(finalStatus.remainingTime));
                    });
                    if (status.gameStarted) {
                        Platform.runLater(() -> controller.gameStarted(finalStatus.text));
                        break;
                    }
                } catch (ClassNotFoundException | StreamCorruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    System.out.println("Server is down!");
                    Platform.runLater(controller::serverDisconnected);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
