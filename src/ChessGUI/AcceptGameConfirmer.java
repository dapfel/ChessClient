package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessGame.PlayerColor;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author dapfel
 */
public class AcceptGameConfirmer extends TimerTask {

    private final HomePage homePage;
    private final Timer timer;
    private int attempts; // after 3 confirmation (with other player) attempts, cancel the game 
    private int ioExceptions; //after 3 IO exceptions, cancel the game
    private final ListeningExecutorService pool;
    private final ProgressDialog progressDialog;
    
    public AcceptGameConfirmer(HomePage homePage, Timer timer, ProgressDialog progressDialog) {
        this.homePage = homePage;
        this.timer = timer;
        this.pool = homePage.getPool();
        this.progressDialog = progressDialog;
        attempts = 0;
        ioExceptions = 0;
    }
    
    @Override
    public void run() { 
        ListenableFuture<String> result = pool.submit(new ServerNegotiationTask(Task.BLACK_START_GAME, null));
        Futures.addCallback(
            result,
            new FutureCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Platform.runLater( () -> {
                        progressDialog.close();
                        completeAcceptingGame(response, progressDialog);
                    });
                }
                @Override
                public void onFailure(Throwable thrown) {
                    ioExceptions++;
                    if (ioExceptions == 3) {//after 3 IO exceptions, cancel the game
                        cancelGame("IOException", progressDialog);   
                    }
                }
            },
            pool);
    }
    
    private void completeAcceptingGame(String response, ProgressDialog progressDialog) {
            if ("success".equals(response)) {
                timer.cancel();
                GamePage gamePage = new GamePage(new ChessGame(PlayerColor.BLACK));
                Platform.runLater(() -> homePage.getPrimaryStage().setScene(gamePage.getGameScene()));
            }
            else { // opponent has not confirmed this game request yet
                attempts++;
                if (attempts == 3)
                    cancelGame("noConfirmation", progressDialog);
            }
    }
    
    private void cancelGame(String failureReason, ProgressDialog progressDialog) {
        Platform.runLater (() -> progressDialog.close());
        String requestingUser = ServerNegotiationTask.getGameRequest().getRequestingUser(); 
        if (failureReason.equals("noConfirmation")) {
            Platform.runLater(() -> {
                homePage.getRequestingPlayersList().getItems().remove(requestingUser); 
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Player Unavailable");
                alert.setHeaderText(null);
                alert.setContentText("The request you have accepted is no longer available. Please accept a different game request.");
                alert.showAndWait();
            });
        }
        else { // "IOException"
            Platform.runLater(() -> { 
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Starting Game");
                alert.setHeaderText(null);
                alert.setContentText("An error occured in starting the game. Please try again.");
                alert.showAndWait();
            });
            // reset client to available in server (in case confirmation succeeded in server but IO exception back to client
            Platform.runLater(() -> homePage.reset("onlyAvailable")); 
        }
        timer.cancel();
        homePage.startRefreshTimers(); 
    }
}