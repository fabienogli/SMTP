package Interface.main;

import Interface.Client;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import smtp.ClientSmtp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;


public class Main {

    private App app;
    private boolean received = true;

    @FXML
    private TableView<ModelMail> mails;

    @FXML
    private TableColumn<ModelMail, String> emailColumn;

    @FXML
    private TableColumn<ModelMail, String> subjectColumn;

    @FXML
    private TableColumn<ModelMail, String> dateColumn;

    @FXML
    private Button sentModeButton;

    @FXML
    private Button receivedModeButton;

    @FXML
    public void initialize() {
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        mails.setRowFactory( tv -> {
            TableRow<ModelMail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ModelMail rowData = row.getItem();
                    this.app.showMail(rowData);
                    System.out.println(rowData);
                }
            });
            return row ;
        });
    }

    public void setApp(App app) {
        this.app = app;
        mails.setItems(this.app.getMails());
    }

    @FXML
    void logout(ActionEvent event) {
        this.app.logout();
    }

    @FXML
    void newMessage(ActionEvent event) {
        this.app.writeMessage();
    }

    public void fulfillColumn() {

    }

    @FXML
    void getReceivedMessages(ActionEvent event) {
        this.receivedModeButton.setDisable(true);
        this.sentModeButton.setDisable(false);
        this.app.setReceivedMode();
        received = true;
    }

    @FXML
    void getSentMessages(ActionEvent event) {
        this.receivedModeButton.setDisable(false);
        this.sentModeButton.setDisable(true);
        this.app.setSentMode();
        received = false;
    }

    @FXML
    void refresh(ActionEvent event) {
        if (received) {
            this.app.setReceivedMode();
        } else {
            this.app.setSentMode();
        }
    }

}
