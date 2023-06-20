package ru.hse.klavogonki.client.room;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.hse.klavogonki.client.game.GameController;
import ru.hse.klavogonki.client.net.ClientSideConnection;
import ru.hse.klavogonki.models.PlayerInfo;

import java.io.IOException;
import java.util.ArrayList;

public class InRoomController {
    private final ObservableList<String> playerList = FXCollections.observableList(new ArrayList<>());
    private final ClientSideConnection connection;
    private final PlayerInfo player;
    private Stage mainMenuStage;
    private Stage inRoomStage;
    @FXML
    private ListView<String> listPlayers;
    @FXML
    private Text timeLeftField;

    public InRoomController(ClientSideConnection connection, PlayerInfo player) {
        this.connection = connection;
        this.player = player;
    }

    public void gameStarted(String text) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
        loader.setControllerFactory(type -> new GameController(connection, text));
        try {
            Parent gameWindow = loader.load();
            GameController controller = loader.getController();
            controller.setMainMenuStage(mainMenuStage);
            Stage gameStage = new Stage();
            controller.setGameStage(gameStage);
            gameStage.setScene(new Scene(gameWindow));
            gameStage.show();
            gameStage.setMinHeight(gameStage.getHeight());
            gameStage.setMinWidth(gameStage.getWidth());
            inRoomStage.hide();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serverDisconnected() {
        inRoomStage.close();
        mainMenuStage.show();
    }

    @FXML
    private void initialize() {
        listPlayers.setItems(playerList);
        new Thread(new InRoomMessageHandler(playerList, timeLeftField, player, connection, this)).start();
    }

    public void setMainMenuStage(Stage mainMenuStage) {
        this.mainMenuStage = mainMenuStage;
    }

    public void setInRoomStage(Stage inRoomStage) {
        this.inRoomStage = inRoomStage;
    }
}
