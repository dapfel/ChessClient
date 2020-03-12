package ChessGUI;

import ChessGUI.ChessClientApp.Page;
import ChessGameLogic.ChessGame;
import ChessGameLogic.SavedGame;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
    
    private final Scene loginScene;
    private static ListeningExecutorService pool;
    private static Stage primaryStage;
    
    public LoginPage() {
        ChessClientApp.setCurrentPage(Page.LOGIN);
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

        Button signInButton = new Button("Sign In");
        signInButton.setOnMouseClicked(mouseEvent -> {
            if (!usernameField.getText().equals("") && !passwordField.getText().equals("")) {
                ProgressDialog progressDialog = new ProgressDialog("Signing In");
                progressDialog.show();
                
                String[] params = {usernameField.getText(), passwordField.getText()};
                ServerNegotiationTask task = new ServerNegotiationTask(Task.SIGN_IN, params);
                ListenableFuture<String> result = pool.submit(task);
                Futures.addCallback(
                    result,
                    new FutureCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            Platform.runLater( () -> {
                                progressDialog.close();
                                completeSignIn(response);
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
            else { // username or password field is empty
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password. Please try again.");
                alert.showAndWait();
            }
        });
        
        grid.add(signInButton, 1, 2);
        
        Button registerButton = new Button("Register");
        registerButton.setOnMouseClicked(mouseEvent -> {
            Scene registerScene = new RegisterPage().getRegisterScene();
            primaryStage.setScene(registerScene);
            primaryStage.show();
        });
        grid.add(registerButton, 1, 3);
        
        loginScene = new Scene(grid, 400, 200);
    }
    
    private void completeSignIn(String response) {
        Alert alert;
        if ("success".equals(response)) {
            SavedGame savedGame = ChessClientApp.getSavedGame();
            if (savedGame != null) { // load a saved game if exists
                initializeSavedGame(savedGame);
                GamePage gamePage = new GamePage(savedGame.getGamePage());
                setTurnLabel(gamePage.getChessGame());
                primaryStage.setScene(gamePage.getGameScene());
            }
            else { // no saved game. go to Home page
                HomePage homePage = new HomePage(ServerNegotiationTask.getUser());
                primaryStage.setScene(homePage.getHomeScene());
                primaryStage.show();
                homePage.startRefreshTimers();
            }
        }
        else if (response.equals("failure")) {
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
    
    private void initializeSavedGame(SavedGame savedGame) {
        ServerNegotiationTask.setOpponent(savedGame.getOpponent());
        ServerNegotiationTask.setGame(savedGame.getGame());
        ChessGame chessGame = savedGame.getGamePage().getChessGame();
        chessGame.loadPieceImages();
        chessGame.setMoveProperty(new SimpleStringProperty());
        chessGame.setTurnProperty(new SimpleStringProperty());
        chessGame.setGameOverProperty(new SimpleStringProperty());
        chessGame.setBoardGUI(new ChessBoardGUI(chessGame.getPlayerColor(), chessGame));
    }
    
    private void setTurnLabel(ChessGame chessGame) {
        chessGame.getTurnProperty().set("Turn: " + chessGame.getTurn().toString());
    }

    public Scene getLoginScene() {
        return loginScene;
    }
    
    
}
