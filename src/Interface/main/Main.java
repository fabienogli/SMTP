package Interface.main;

import smtp.ClientSmtp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class Main {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void logout(ActionEvent event) {
        ClientSmtp clientSmtp = app.getClientSmtp();
        clientSmtp.quit();
    }

    @FXML
    void newMessage(ActionEvent event) {
        this.app.writeMessage();
    }

}
