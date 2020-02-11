package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ServerNegotiationTask;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author dapfel
 */
public class AcceptGameConfirmer extends TimerTask {

    private HomePage homePage;
    private Timer timer;
    private int attempts;
    
    public AcceptGameConfirmer(HomePage homePage, Timer timer) {
        this.homePage = homePage;
        this.timer = timer;
        attempts = 0;
    }
    
    @Override
    public void run() {
        
        ServerNegotiationTask confirmAndStartGameTask = new ServerNegotiationTask("blackStartGame", null);
        String requestingUser = confirmAndStartGameTask.getGameRequest().getGamerequestPK().getRequestingUser();
        
        while (attempts < 3) { // after 3 confirmation attempts cancel the game
            Future<String> result = homePage.getPool().submit(confirmAndStartGameTask);
            try {
                if (result.get().equals("success")) {
                    GamePage gamePage = new GamePage(homePage.getPrimaryStage(), homePage.getPool(), new ChessGame());
                    Platform.runLater(() -> homePage.getPrimaryStage().setScene(gamePage.getGameScene()));
                    timer.cancel();
                    break;
                }
                else // opponent has not confirmed this game request yet
                    attempts++;
            }
            catch (InterruptedException | ExecutionException e) {
                if (attempts == 2) {
                    Platform.runLater(() -> {
                        homePage.getRequestingPlayersList().getItems().remove(requestingUser); 
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Starting Game");
                        alert.setHeaderText(null);
                        alert.setContentText("An error occured in starting the game. Please try again.");
                        alert.showAndWait();
                    });
                    timer.cancel();
                    homePage.startRefreshTimers();   
                }
                attempts++;
            }
        }
        Platform.runLater(() -> {
            homePage.getRequestingPlayersList().getItems().remove(requestingUser); 
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Player Unavailable");
            alert.setHeaderText(null);
            alert.setContentText("The request you have accepted is no longer available. Please accept a different game request.");
            alert.showAndWait();
        });
        timer.cancel();
        homePage.startRefreshTimers();
    }
}
