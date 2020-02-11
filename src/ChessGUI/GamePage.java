package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ServerNegotiationTask;
import ServerAccess.User;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    ExecutorService pool;
    private final int gameID;
    private Scene gameScene;
    private final ChessGame chessGame;
    private User user;
    private String opponent;
    
    public GamePage(Stage primaryStage, ExecutorService pool, ChessGame chessGame) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            Future<String> result = pool.submit(new ServerNegotiationTask("endGame", new String[0]));
            
            try {
                if (result.get().equals("success")) {
                    HomePage homePage = new HomePage(primaryStage, pool, user);
                    primaryStage.setScene(homePage.getHomeScene());
                    primaryStage.show();
                    homePage.startRefreshTimers();
                }
                else {// IOException
                    alert.setTitle("Connection Error");
                    alert.setContentText("Error connecting to Server. Please try again.");
                    alert.showAndWait();
                }                          
            }
            catch (InterruptedException | ExecutionException e) {
                    alert.setTitle("Execution Error");
                    alert.setContentText("An error has occured in proccessing your request. Please try again.");
                    alert.showAndWait();  
            }
        });
        
        topButtonsHbox.getChildren().addAll(endGameButton);
        
        gameScene = new Scene(border, 600, 300);
        
        //when other finds game deleted (lastMove or make move returns null), just give message that other player ended game and then go to homepage
                
       // after every Move of black or white, save to savedGame of ChessClient
    }

    public Scene getGameScene() {
        return gameScene;
    }

}
