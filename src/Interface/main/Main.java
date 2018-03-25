package Interface.main;

import Interface.Client;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

}
