package ru.hse.klavogonki.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.klavogonki.client.net.ClientSideConnection;

import java.io.IOException;

public class Client extends Application {
    private static final ClientSideConnection connection = new ClientSideConnection();

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-menu.fxml"));
        loader.setControllerFactory(type -> new MainMenuController(connection));
        Parent root = loader.load();
        MainMenuController controller = loader.getController();
        controller.setMainMenuStage(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMaxWidth(primaryStage.getWidth());
        primaryStage.setMaxHeight(primaryStage.getHeight());
    }

    @Override
    public void stop() throws IOException {
        connection.close();
    }
}
