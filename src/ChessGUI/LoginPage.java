package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.SavedGame;
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
 * @author user
 */
public class LoginPage {
    
    private Scene loginScene;
    
    public LoginPage(Stage primaryStage, ExecutorService pool) {
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

        Button signInButton = new Button("Sign In");
        signInButton.setOnMouseClicked(mouseEvent -> {
            String[] params = {usernameField.getText(), passwordField.getText()};
            ServerNegotiationTask task = new ServerNegotiationTask("signIn", params);
            Future<String> result = pool.submit(task);
            Alert alert;
            try {
                if (result.get().equals("success")) {
                    SavedGame savedGame = ChessClient.getSavedGame();
                    if (savedGame != null) { // load a saved game if exists
                        ServerNegotiationTask.setOpponent(savedGame.getOpponent());
                        ServerNegotiationTask.setGame(savedGame.getGame());
                        GamePage gamePage = new GamePage(primaryStage, pool, savedGame.getChessGame());
                        primaryStage.setScene(gamePage.getGameScene());
                    }
                    else { // no saved game. go to Home page
                        HomePage homePage = new HomePage(primaryStage, pool, ServerNegotiationTask.getUser());
                        primaryStage.setScene(homePage.getHomeScene());
                        primaryStage.show();
                        homePage.startRefreshTimers();
                    }
                }
                else if (result.get() == null) {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid username or password. Please try again.");
                    alert.showAndWait();
                }
                else {//IOException
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
        });
        
        grid.add(signInButton, 1, 2);
        
        Button registerButton = new Button("Register");
        registerButton.setOnMouseClicked(mouseEvent -> {
            Scene registerScene = new RegisterPage(primaryStage, pool).getRegisterScene();
            primaryStage.setScene(registerScene);
            primaryStage.show();
        });
        grid.add(registerButton, 1, 3);
        
        loginScene = new Scene(grid, 400, 200);
    }

    public Scene getLoginScene() {
        return loginScene;
    }
    
    
}
