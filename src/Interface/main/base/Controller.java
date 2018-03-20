package Interface.main.base;

import Interface.main.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class Controller {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void about(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {

    }

    @FXML
    void deleteMessage(ActionEvent event) {

    }

    @FXML
    void newMessage(ActionEvent event) {
        this.app.writeMessage();
    }

    @FXML
    void quit(ActionEvent event) {
        this.app.getPrimaryStage().close();
    }
}
