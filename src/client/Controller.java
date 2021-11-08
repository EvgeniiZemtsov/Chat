package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox messagePanel;
    @FXML
    public ListView<String> clientList;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Stage stage;
    private Stage regStage;
    private RegController regController;

    private boolean isAuthenticated;
    private String nickname;
    private final String TITLE = "The chat";

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;

        authPanel.setVisible(!isAuthenticated);
        authPanel.setManaged(!isAuthenticated);

        messagePanel.setVisible(isAuthenticated);
        messagePanel.setManaged(isAuthenticated);

        clientList.setVisible(isAuthenticated);
        clientList.setManaged(isAuthenticated);

        if (!isAuthenticated) {
            nickname = "";
        }

        setTitle(nickname);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        createRegWindow();
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (socket != null && !socket.isClosed()) {
                    try {
                        outputStream.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            outputStream.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuthenticate(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            outputStream.writeUTF(String.format("/auth %s %s", loginField.getText().toLowerCase(Locale.ROOT).trim(), passwordField.getText().trim()));
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //authentication cycle
                    while (true) {
                        String message = inputStream.readUTF();
                        if (message.startsWith("/authok")) {
                            nickname = message.split(" ", 2)[1];
                            setAuthenticated(true);
                            break;
                        }

                        if (message.startsWith("/regok")) {
                            regController.addMessageToTextField("The user has been successfully registered.");
                        }

                        if (message.startsWith("/regno")) {
                            regController.addMessageToTextField("The registration failed \nProbably user with the same login or nickname already exists.");
                        }
                        textArea.appendText(message + "\n");
                    }
                    //work cycle
                    while (true) {
                        String message = inputStream.readUTF();

                        if (message.startsWith("/")) {
                            if (message.equals("/end")) {
                                break;
                            }
                            if (message.startsWith("/clientsList ")) {
                                String[] token = message.split("\\s+");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                        } else {
                            textArea.appendText(message + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("The client has been disconnected");
                    setAuthenticated(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nick) {
        Platform.runLater(() -> {
            ((Stage) textField.getScene().getWindow()).setTitle(TITLE + " " + nick);
        });
    }

    public void clickClientsList(MouseEvent mouseEvent) {
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w " + " " + receiver + " ");
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registration.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("Registration");
            regStage.setScene(new Scene(root, 400, 250));

            regController = fxmlLoader.getController();
            regController.setController(this);

            regStage.initModality(Modality.APPLICATION_MODAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registration(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToSignIn(String login, String password, String nickname) {
        String message = String.format("/reg %s %s %s", login, password, nickname);

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
