package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ServerNegotiationTask;
import ServerAccess.User;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private final ChessGame chessGame;
    private User user;
    private final String opponent;
    private final ChessBoardPane chessBoardPane;
    
    public GamePage(Stage primaryStage, ListeningExecutorService pool, ChessGame chessGame) {
        this.user = ServerNegotiationTask.getUser();
        this.gameID = ServerNegotiationTask.getGame().getGameID();
        this.chessGame = chessGame;
        this.primaryStage = primaryStage;
        this.pool = pool;
        this.opponent = ServerNegotiationTask.getOpponent();
        
        BorderPane border = new BorderPane();
        
        HBox topButtonsHbox = new HBox();
        topButtonsHbox.setPadding(new Insets(15, 12, 15, 12));
        topButtonsHbox.setSpacing(10);
        border.setTop(topButtonsHbox);
        
        Button endGameButton = new Button("End Game");
        endGameButton.setPrefSize(100, 20);
        endGameButton.setOnMouseClicked(mouseEvent -> {  
            ProgressDialog progressDialog = new ProgressDialog("Ending Game");
            progressDialog.show();
            
            ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("endGame", new String[0]));
            Futures.addCallback(
                result,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            completeEndingGame(response, primaryStage, pool);
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
        
        topButtonsHbox.getChildren().addAll(endGameButton);
        
        chessBoardPane = new ChessBoardPane(chessGame.getPlayerColor());
        border.setCenter(chessBoardPane);
        
        gameScene = new Scene(border, 600, 300);
        
        //when other finds game deleted (lastMove or make move returns null), just give message that other player ended game and then go to homepage
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                
       // after every Move of black or white, save to savedGame of ChessClient. when game ove make savedgame null
    }
    
    private void completeEndingGame(String response, Stage primaryStage, ListeningExecutorService pool) {
            if ("success".equals(response)) {
                HomePage homePage = new HomePage(primaryStage, pool, user);
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

    public Scene getGameScene() {
        return gameScene;
    }

}
