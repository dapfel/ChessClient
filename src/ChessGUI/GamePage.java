package ChessGUI;

import ChessGUI.ChessClientApp.Page;
import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessGame.PlayerColor;
import ChessGameLogic.SavedGame;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
import ServerAccess.User;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Timer;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author dapfel
 */
public class GamePage {

    private final Stage primaryStage;
    private final ListeningExecutorService pool;
    private final int gameID;
    private final Scene gameScene;
    private ChessGame chessGame;
    private final User user;
    private final String opponent;
    private final LastMoveGetter lastMoveGetter;
    private final Timer lastMoveTimer;
    private Label turnLabel;
        
    public GamePage(ChessGame chessGame) {
        ChessClientApp.setCurrentPage(Page.GAME);
        this.user = ServerNegotiationTask.getUser();
        this.gameID = ServerNegotiationTask.getGame().getGameID();
        this.chessGame = chessGame;
        pool = ChessClientApp.getPool();
        primaryStage = ChessClientApp.getPrimaryStage();
        this.opponent = ServerNegotiationTask.getOpponent();
        
        StringProperty moveProperty = chessGame.getMoveProperty();
        setMovePropertyListener(moveProperty);
        
        lastMoveTimer = new Timer();
        lastMoveGetter = new LastMoveGetter(this, lastMoveTimer);
        if (chessGame.getPlayerColor().equals(PlayerColor.BLACK))
            lastMoveTimer.schedule(lastMoveGetter, 5000, 5000);
        
        BorderPane border = new BorderPane();
        border.setTop(topHbox());
        border.setCenter(chessGame.getBoardGUI().getChessBoardPane());
        
        gameScene = new Scene(border);
    }
    
    public GamePage(GamePage gamePage) {
        this(gamePage.getChessGame());
        turnLabel.setText("TURN: " + chessGame.getTurn());
        if (gamePage.getChessGame().getTurn().equals(chessGame.getPlayerColor())) {
            lastMoveTimer.cancel();
        }
        else {
            lastMoveTimer.schedule(lastMoveGetter, 5000, 5000);
            chessGame.freezeBoard();
        }
    }
    
    private HBox topHbox() {
        HBox topHbox = new HBox();
        topHbox.setPadding(new Insets(15, 12, 15, 12));
        topHbox.setSpacing(10);
        
        Button endGameButton = new Button("End Game");
        endGameButton.setPrefSize(100, 20);
        endGameButton.setOnMouseClicked(mouseEvent -> {  
            ProgressDialog progressDialog = new ProgressDialog("Ending Game");
            progressDialog.show();
            
            ListenableFuture<String> result = pool.submit(new ServerNegotiationTask(Task.END_GAME, new String[0]));
            Futures.addCallback(
                result,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            completeEndingGame(response);
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
        });
        
        Label gameLabel = new Label(user.getUsername() + " vs " + opponent);
        
        turnLabel = new Label("TURN: " + chessGame.getTurn());
        turnLabel.textProperty().bind(chessGame.getTurnProperty());
        
        topHbox.getChildren().addAll(endGameButton, gameLabel, turnLabel);
        return topHbox;
    }                  
        
    /*
    when client player makes move on GUI, get the move and send it to the server for opponent
    */
    private void setMovePropertyListener(StringProperty moveProperty) {
        moveProperty.addListener((ChangeEvent) -> {
            ProgressDialog progressDialog = new ProgressDialog("Sending Move");
            progressDialog.show();
            String[] params = {moveProperty.getValue()};
            ListenableFuture<String> result = pool.submit(new ServerNegotiationTask(Task.MAKE_MOVE, params));
            Futures.addCallback(
                result,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            completeMakeMove(response);
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
        });
    }
    
    private void completeEndingGame(String response) {
        if ("success".equals(response)) {
            ChessClientApp.setSavedGame(null);
            HomePage homePage = new HomePage(user);
            primaryStage.setScene(homePage.getHomeScene());
            primaryStage.show();
            homePage.startRefreshTimers();
        }
        else {// IOException
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Connection Error");
            alert.setContentText("Error connecting to Server. Please try again.");
            alert.showAndWait();
            }         
    }
    
    private void completeMakeMove(String response) {
        if ("success".equals(response)) { 
            ChessClientApp.setSavedGame(new SavedGame(this, opponent));
            lastMoveTimer.schedule(lastMoveGetter, 5000, 5000);
        }
        else if ("IOException".equals(response)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Error connecting to Server. Please try again.");
            alert.showAndWait();
        } 
        else if ("failure".equals(response)) { //opponent ended the game
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Ended");
            alert.setContentText("Your opponent has ended the game. Press the End Game button to return to the home page.");
            alert.showAndWait();            
        }
    }
    
    public Scene getGameScene() {
        return gameScene;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
    
    public String getOpponent() {
        return opponent;
    }
}
