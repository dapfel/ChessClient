package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessGame.PlayerColor;
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
    private int ioExceptions;
    
    public AcceptGameConfirmer(HomePage homePage, Timer timer) {
        this.homePage = homePage;
        this.timer = timer;
        attempts = 0;
    }
    
    @Override
    public void run() {
        
        String requestingUser = ServerNegotiationTask.getGameRequest().getGamerequestPK().getRequestingUser();   
        while (attempts < 3) { // after 3 confirmation (with other player) attempts, cancel the game 
            Future<String> result = homePage.getPool().submit(new ServerNegotiationTask("blackStartGame", null));
            try {
                if (result.get().equals("success")) {
                    GamePage gamePage = new GamePage(homePage.getPrimaryStage(), homePage.getPool(), new ChessGame(PlayerColor.BLACK));
                    Platform.runLater(() -> homePage.getPrimaryStage().setScene(gamePage.getGameScene()));
                    timer.cancel();
                    break;
                }
                else // opponent has not confirmed this game request yet
                    attempts++;
            }
            catch (InterruptedException | ExecutionException e) {
                if (ioExceptions == 2) { //after 3 IO exceptions, cancel the game
                    Platform.runLater(() -> {
                        homePage.getRequestingPlayersList().getItems().remove(requestingUser); 
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Starting Game");
                        alert.setHeaderText(null);
                        alert.setContentText("An error occured in starting the game. Please try again.");
                        alert.showAndWait();
                    });
                    timer.cancel();
                    Platform.runLater(() -> homePage.reset("onlyAvailable"));
                    homePage.startRefreshTimers();   
                }
                ioExceptions++;
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
