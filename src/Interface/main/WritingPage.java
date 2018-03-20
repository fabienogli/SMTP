package Interface.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WritingPage {

    @FXML
    private Button sendButton;

    @FXML
    private Button attachButton;

    @FXML
    private Button SaveButton;

    @FXML
    private TextField senderTextField;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea corpsTextArea;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }
}
