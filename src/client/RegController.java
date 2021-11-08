package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    private Controller controller;
    @FXML
    private TextField loginField;
    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea textArea;

    public void tryToSignIn(ActionEvent actionEvent) {
        controller.tryToSignIn(loginField.getText().trim(),
                passwordField.getText().trim(),
                nicknameField.getText().trim());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addMessageToTextField(String message) {
        textArea.appendText(message + "\n");
    }
}
