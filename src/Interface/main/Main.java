package Interface.main;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Main {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void logout(ActionEvent event) {
        Client client = app.getClient();
        client.quit();
    }

    @FXML
    void newMessage(ActionEvent event) {
        this.app.writeMessage();
    }

}
