package ChessGUI;

import ChessGUI.ChessClientApp.Page;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import javafx.application.Platform;

/**
 *
 * @author dapfel
 */
public class RegisterPage {
    
    private final Scene registerScene;
    private static ListeningExecutorService pool;
    private static Stage primaryStage;
    
    public RegisterPage() {
        ChessClientApp.setCurrentPage(Page.REGISTER);
        pool = ChessClientApp.getPool();
        primaryStage = ChessClientApp.getPrimaryStage();
        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12,12,12,12));
        grid.setMinSize(400,200);

        Label usernameLabel = new Label("Username:");
        usernameLabel.setMinWidth(100);
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);
        grid.add(usernameLabel, 0, 0);

        TextField usernameField = new TextField();
        usernameLabel.setLabelFor(usernameField);
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setMinWidth(100);
        passwordLabel.setAlignment(Pos.CENTER_RIGHT);
        grid.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        passwordLabel.setLabelFor(passwordField);
        grid.add(passwordField, 1, 1);
        
        Label passwordLabel2 = new Label("Re-enter Password:");
        passwordLabel2.setMinWidth(100);
        passwordLabel2.setAlignment(Pos.CENTER_RIGHT);
        grid.add(passwordLabel2, 0, 2);

        PasswordField passwordField2 = new PasswordField();
        passwordLabel2.setLabelFor(passwordField2);
        grid.add(passwordField2, 1, 2);

        Button registerButton = new Button("Register");
        registerButton.setOnMouseClicked(mouseEvent -> {
            Alert alert;
            if (passwordField.getText().equals(passwordField2.getText())) {
                ProgressDialog progressDialog = new ProgressDialog("Registering new User");
                progressDialog.show();
                
                String[] params = {usernameField.getText(), passwordField.getText()};
                ServerNegotiationTask task = new ServerNegotiationTask(Task.ADD_USER, params);
                ListenableFuture<String> result = pool.submit(task);
                Futures.addCallback(
                    result,
                    new FutureCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            Platform.runLater( () -> {
                                progressDialog.close();
                                completeRegistration(response, primaryStage, pool);
                            });
                        }
                        @Override
                        public void onFailure(Throwable thrown) {
                            Platform.runLater( () -> {
                                progressDialog.close();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Execution Error");
                                alert.setHeaderText(null);
                                alert.setContentText("An error has occured in proccessing your request. Please try again.");
                                alert.showAndWait();   
                            });
                        }
                    },
                    pool);
            }
            else {
                alert = new Alert(AlertType.WARNING);
                alert.setTitle("Passwords do not match");
                alert.setHeaderText(null);
                alert.setContentText("Password fields do not match. Please re-enter your passswords.");
                alert.showAndWait();
            }
        });
        grid.add(registerButton, 1, 3);
        
        registerScene = new Scene(grid, 400, 200);
    }
    
    private void completeRegistration(String response, Stage primaryStage, ListeningExecutorService pool) {
        Alert alert;
        if ("success".equals(response)) {
            LoginPage loginPage = new LoginPage();
            primaryStage.setScene(loginPage.getLoginScene());
            primaryStage.show();
        }
        else if (response.equals("failure")) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Register Error");
            alert.setHeaderText(null);
            alert.setContentText("Error registering new User due to invalid input. Please try again.");
            alert.showAndWait();                
        }
        else { //IOException
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Error connecting to Server. Please try again.");
            alert.showAndWait();
        }        
    }

    public Scene getRegisterScene() {
        return registerScene;
    }
    
}
