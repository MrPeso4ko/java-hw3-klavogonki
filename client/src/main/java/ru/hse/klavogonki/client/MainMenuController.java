package ru.hse.klavogonki.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.hse.klavogonki.client.net.ClientSideConnection;
import ru.hse.klavogonki.client.net.Connector;
import ru.hse.klavogonki.client.room.InRoomController;
import ru.hse.klavogonki.models.PlayerInfo;

import java.io.IOException;

public class MainMenuController {
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = 5619;
    private final ClientSideConnection connection;
    public Text errorMessage;
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;

    private Stage mainMenuStage;

    public MainMenuController(ClientSideConnection connection) {
        this.connection = connection;
    }

    private String getHost() {
        if (hostField.getCharacters().isEmpty()) {
            return DEFAULT_HOST;
        }
        return hostField.getCharacters().toString();
    }

    private int getPort() {
        if (portField.getCharacters().isEmpty()) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(portField.getCharacters().toString());
    }

    @FXML
    private void Connect(ActionEvent ignoredActionEvent) {
        Thread thread = new Thread(new Connector(getHost(), getPort(), connection, this));
        thread.start();
    }

    @FXML
    private void showAboutScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/about.fxml"));
        Parent aboutWindow = loader.load();
        Button backButton = (Button) loader.getNamespace().get("backButton");
        Stage aboutStage = new Stage();
        backButton.setOnAction(event -> Platform.runLater(aboutStage::close));
        aboutStage.setScene(new Scene(aboutWindow));
        aboutStage.show();
    }

    public void ConnectionError() {
        errorMessage.setText("Не удалось подключиться к серверу!");
    }

    public void Connected() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/room.fxml"));
        PlayerInfo info = new PlayerInfo();
        info.name = nicknameField.getCharacters().toString();
        loader.setControllerFactory(type -> new InRoomController(connection, info));
        try {
            Parent roomWindow = loader.load();
            InRoomController controller = loader.getController();
            controller.setMainMenuStage(mainMenuStage);
            Stage roomStage = new Stage();
            controller.setInRoomStage(roomStage);
            roomStage.setScene(new Scene(roomWindow));
            roomStage.show();
            roomStage.setMinHeight(roomStage.getHeight());
            roomStage.setMaxHeight(roomStage.getHeight());
            roomStage.setMinWidth(roomStage.getWidth());
            roomStage.setMaxWidth(roomStage.getWidth());
            mainMenuStage.hide();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMainMenuStage(Stage mainMenuStage) {
        this.mainMenuStage = mainMenuStage;
    }
}
