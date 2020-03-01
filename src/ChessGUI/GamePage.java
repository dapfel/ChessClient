package ChessGUI;

import ChessGUI.ChessClientApp.Page;
import ChessGameLogic.ChessGame;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
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
    private static ChessGame chessGame;
    private final User user;
    private final String opponent;
    
    public GamePage(ChessGame chessGame) {
        ChessClientApp.setCurrentPage(Page.GAME);
        this.user = ServerNegotiationTask.getUser();
        this.gameID = ServerNegotiationTask.getGame().getGameID();
        this.chessGame = chessGame;
        pool = ChessClientApp.getPool();
        primaryStage = ChessClientApp.getPrimaryStage();
        this.opponent = ServerNegotiationTask.getOpponent();
        
        BorderPane border = new BorderPane();
        border.setTop(topHbox());
        border.setCenter(chessGame.getBoardGUI().getChessBoardPane());
        
        gameScene = new Scene(border);
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
        
        topHbox.getChildren().addAll(endGameButton, gameLabel);
        return topHbox;
    }

        //when other finds game deleted (lastMove or make move returns null), just give message that other player ended game and then go to homepage
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                
       // after every Move of black or white, save to savedGame of ChessClient. when game ove make savedgame null   

    private void completeEndingGame(String response) {
            if ("success".equals(response)) {
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

    public Scene getGameScene() {
        return gameScene;
    }

    public static ChessGame getChessGame() {
        return chessGame;
    }

}
