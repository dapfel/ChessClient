package ChessGUI;

import ChessGameLogic.ServerNegotiationTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author dapfel
 */
public class UpdateUserPage {
    
    private final Scene updateUserScene;

    public UpdateUserPage(Stage primaryStage, ExecutorService pool) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12,12,12,12));
        grid.setMinSize(400,200);
        
        Label updateUserLabel = new Label("Update your user details. to leave a value the same, leave its field empty");
        updateUserLabel.setMinWidth(100);
        updateUserLabel.setAlignment(Pos.CENTER_RIGHT);
        grid.add(updateUserLabel, 0, 0);

        Label usernameLabel = new Label("New Username:");
        usernameLabel.setMinWidth(100);
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);
        grid.add(usernameLabel, 0, 1);

        TextField usernameField = new TextField();
        usernameLabel.setLabelFor(usernameField);
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("New Password:");
        passwordLabel.setMinWidth(100);
        passwordLabel.setAlignment(Pos.CENTER_RIGHT);
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        passwordLabel.setLabelFor(passwordField);
        grid.add(passwordField, 1, 2);
        
        Label passwordLabel2 = new Label("Re-enter New Password:");
        passwordLabel2.setMinWidth(100);
        passwordLabel2.setAlignment(Pos.CENTER_RIGHT);
        grid.add(passwordLabel2, 0, 3);

        PasswordField passwordField2 = new PasswordField();
        passwordLabel2.setLabelFor(passwordField2);
        grid.add(passwordField2, 1, 3);

        Button registerButton = new Button("Submit");
        registerButton.setOnMouseClicked(mouseEvent -> {
            Alert alert;
            if (passwordField.getText().equals(passwordField2.getText())) {
                String[] params = {usernameField.getText(), passwordField.getText()};
                ServerNegotiationTask task = new ServerNegotiationTask("updateUser", params);
                Future<String> result = pool.submit(task);
                try {
                    if (result.get().equals("success")) {
                        HomePage homePage = new HomePage(primaryStage, pool, ServerNegotiationTask.getUser());
                        primaryStage.setScene(homePage.getHomeScene());
                        primaryStage.show();
                        homePage.startRefreshTimers();
                    }
                    else if (result.get() == null) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update User Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error updating the User due to invalid input. Please try again.");
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
                catch (InterruptedException | ExecutionException e) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Execution Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occured in proccessing your request. Please try again.");
                    alert.showAndWait();  
                }
            }
            else {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Passwords do not match");
                alert.setHeaderText(null);
                alert.setContentText("Password fields do not match. Please re-enter your passswords.");
                alert.showAndWait();
            }
        });
        grid.add(registerButton, 1, 3);
        
        updateUserScene = new Scene(grid, 400, 200);
    }

    public Scene getUpdateUserScene() {
        return updateUserScene;
    }          
}
