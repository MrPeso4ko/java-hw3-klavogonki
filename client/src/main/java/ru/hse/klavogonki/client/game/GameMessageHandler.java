package ru.hse.klavogonki.client.game;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.hse.klavogonki.client.net.ClientSideConnection;
import ru.hse.klavogonki.models.GameStatus;
import ru.hse.klavogonki.models.PlayerInfo;
import ru.hse.klavogonki.models.Timings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class GameMessageHandler implements Runnable {
    private final ClientSideConnection connection;
    private final ObservableList<String> playersStatuses;
    private final GameController controller;
    private final Text remainingTimeTitle;
    private final Text remainingTimeField;
    private final String text;
    private final TextField inputTextField;

    public GameMessageHandler(ClientSideConnection connection, ObservableList<String> playersStatuses, GameController controller, Text remainingTimeTitle, Text remainingTimeField, TextField inputTextField, String text) {
        this.connection = connection;
        this.playersStatuses = playersStatuses;
        this.controller = controller;
        this.remainingTimeTitle = remainingTimeTitle;
        this.remainingTimeField = remainingTimeField;
        this.inputTextField = inputTextField;
        this.text = text;
    }

    private PlayerInfo prepareInfoToSend() {
        PlayerInfo info = new PlayerInfo();
        info.written = controller.getWritten();
        info.mistakes = controller.getMistakes();
        return info;
    }

    private void sendInfo() throws IOException {
        ObjectOutputStream outputStream = connection.getOutputStream();
        PlayerInfo info = prepareInfoToSend();
        outputStream.writeObject(info);
    }

    private String stringInfo(PlayerInfo player, GameStatus status) {
        String statusString = player.name + ' ' + (100 * player.written / text.length()) + "%, " + player.mistakes;
        if (player.mistakes != 11 && player.mistakes % 10 == 1) {
            statusString += " ошибка, ";
        } else if (player.mistakes != 12 && player.mistakes != 13 && player.mistakes != 14 &&
                (player.mistakes % 10 == 2 || player.mistakes % 10 == 3 || player.mistakes % 10 == 4)) {
            statusString += " ошибки, ";
        } else {
            statusString += " ошибок, ";
        }
        if (status.remainingTime != Timings.GAME_DURATION && status.GameStarted) {
            statusString += (60L * player.written / (Timings.GAME_DURATION - status.remainingTime)) + " сим/мин";
        }
        if (player.isYou) {
            statusString += " - это вы";
        }
        if (player.disconnected) {
            statusString += " - отключился";
        }
        return statusString;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream = connection.getInputStream();
        while (true) {
            try {
                GameStatus status = (GameStatus) inputStream.readObject();
                if (!status.GameStarted) {
                    Platform.runLater(() -> remainingTimeField.setText(status.remainingTime + " секунд"));
                } else {
                    Platform.runLater(() -> {
                        inputTextField.setEditable(true);
                        remainingTimeTitle.setText("До конца гонки осталось:");
                        remainingTimeField.setText(status.remainingTime + " секунд");
                    });
                }
                List<String> preparedPlayersStatuses = new ArrayList<>();
                for (PlayerInfo player : status.players) {
                    preparedPlayersStatuses.add(stringInfo(player, status));
                    Platform.runLater(() -> {
                        playersStatuses.clear();
                        playersStatuses.addAll(preparedPlayersStatuses);
                    });
                    if (status.GameStarted) {
                        sendInfo();
                    }
                }
                if (status.GameFinished) {
                    Platform.runLater(() -> controller.gameFinished(status.players.get(0).isYou, false));
                }
            } catch (ClassNotFoundException | StreamCorruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.out.println("Server is down!");
                Platform.runLater(() -> controller.gameFinished(false, true));
                break;
            }
        }
    }
}
