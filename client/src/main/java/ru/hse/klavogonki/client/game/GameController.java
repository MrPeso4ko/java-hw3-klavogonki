package ru.hse.klavogonki.client.game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.hse.klavogonki.client.net.ClientSideConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {
    private final ObservableList<String> playerStatuses = FXCollections.observableList(new ArrayList<>());
    private final ClientSideConnection connection;
    private final String text;
    private final List<String> words;
    private final AtomicInteger written = new AtomicInteger();
    private final AtomicInteger mistakes = new AtomicInteger();
    public Button playAgainButton;
    private int currentWord = 0;
    private int mistakePosition = -1;
    private Stage gameStage;
    private Stage mainMenuStage;
    @FXML
    private Text remainingTimeTitle;
    @FXML
    private ListView<String> listPlayersStatus;
    @FXML
    private Text remainingTimeField;
    @FXML
    private TextArea gameTextArea;
    @FXML
    private TextField inputTextField;

    public GameController(ClientSideConnection connection, String text) {
        this.text = text;
        words = List.of(text.split(" "));
        this.connection = connection;
    }


    @FXML
    private void initialize() {
        listPlayersStatus.setItems(playerStatuses);
        gameTextArea.setText(text);
        new Thread(new GameMessageHandler(connection, playerStatuses, this, remainingTimeTitle, remainingTimeField, inputTextField, text)).start();
    }

    public void setMainMenuStage(Stage mainMenuStage) {
        this.mainMenuStage = mainMenuStage;
    }

    public void setGameStage(Stage gameStage) {
        this.gameStage = gameStage;
    }

    @FXML
    private void handleNewKey(KeyEvent event) {
        if (!inputTextField.isEditable() || currentWord == words.size()) {
            return;
        }
        int currentLength = inputTextField.getCharacters().length();
        String c = event.getCharacter();
        if (c.equals("\b")) {
            if (currentLength == mistakePosition) {
                mistakeFixed();
            }
        } else if (c.equals(" ")) {
            if (mistakePosition == -1) {
                if (currentLength == words.get(currentWord).length() + 1) {
                    inputTextField.clear();
                    ++currentWord;
                } else {
                    mistakeHappened(currentLength);
                }
            }
        } else {
            if (mistakePosition == -1) {
                if (currentLength - 1 < words.get(currentWord).length() &&
                        c.equals(Character.toString(words.get(currentWord).charAt(currentLength - 1)))) {
                    written.incrementAndGet();
                } else {
                    mistakeHappened(currentLength);
                }
            }
        }
    }

    public void gameFinished(boolean win, boolean serverDown) {
        playAgainButton.setVisible(true);
        playAgainButton.setDisable(false);
        if (serverDown) {
            remainingTimeTitle.setText("Потеряно соединение с сервером\nИгра окончена");
        } else {
            remainingTimeTitle.setText("Игра окончена");
        }
        if (win) {
            remainingTimeField.setText("Поздравляем! Хотите сыграть ещё раз?");
        } else {
            remainingTimeField.setText("Хотите сыграть ещё раз?");
        }
        inputTextField.setDisable(true);
        inputTextField.setEditable(false);
    }

    private void mistakeHappened(int position) {
        mistakePosition = position - 1;
        mistakes.incrementAndGet();
        inputTextField.setStyle("-fx-text-fill: red; -fx-border-color: red");
    }

    private void mistakeFixed() {
        mistakePosition = -1;
        inputTextField.setStyle("");
    }

    public int getWritten() {
        return written.get();
    }

    public int getMistakes() {
        return mistakes.get();
    }

    @FXML
    private void playAgain(ActionEvent event) {
        gameStage.hide();
        mainMenuStage.show();
    }
}