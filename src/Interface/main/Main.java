package Interface.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Main {

    @FXML
    private Button draftButton;

    @FXML
    private Button sentMessagesButton;

    @FXML
    private Button newMessageButton;

    @FXML
    private Button logoutButton;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

}
