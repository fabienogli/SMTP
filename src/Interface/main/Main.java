package Interface.main;

import javafx.scene.control.TableColumn;
import smtp.ClientSmtp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;


public class Main {

    private App app;

    @FXML
    TableView<ModelMail> mails;

    @FXML
    private TableColumn<ModelMail, String> emailColumn;

    @FXML
    private TableColumn<ModelMail, String> subjectColumn;

    @FXML
    public void initialize() {
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
    }

    public void setApp(App app) {
        this.app = app;
        mails.setItems(this.app.getMails());
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

    public void fulfillColumn() {

    }

}
