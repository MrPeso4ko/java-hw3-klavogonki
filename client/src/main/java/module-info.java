module ru.hse.klavogonki.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires ru.hse.klavogonki.models;

    exports ru.hse.klavogonki.client;
    exports ru.hse.klavogonki.client.net;
    exports ru.hse.klavogonki.client.room;
    exports ru.hse.klavogonki.client.game;
    opens ru.hse.klavogonki.client to javafx.fxml;
    opens ru.hse.klavogonki.client.room to javafx.fxml;
    opens ru.hse.klavogonki.client.game to javafx.fxml;
}