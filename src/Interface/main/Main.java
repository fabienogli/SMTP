package Interface.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Main {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void drafts(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void newMessage(ActionEvent event) {
        this.app.writeMessage();
    }

    @FXML
    void sentMessages(ActionEvent event) {

    }

}
