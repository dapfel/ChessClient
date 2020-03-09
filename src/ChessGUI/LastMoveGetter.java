package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.SavedGame;
import ServerAccess.ServerNegotiationTask;
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
public class LastMoveGetter extends TimerTask {
    
    private final GamePage gamePage;
    private final ListeningExecutorService pool;
    private final ChessGame chessGame;
    private final Timer lastMoveTimer;
    private final String opponent;
    
    public LastMoveGetter(GamePage gamePage, Timer lastMoveTimer) {
        this.gamePage = gamePage;
        pool = ChessClientApp.getPool();
        this.chessGame = gamePage.getChessGame();
        this.lastMoveTimer = lastMoveTimer;
        this.opponent = gamePage.getOpponent();
    }

    @Override
    public void run() {
        final String previousMove;
        if (ServerNegotiationTask.getGame() != null)
            previousMove = ServerNegotiationTask.getGame().getMove();
        else
            previousMove = null;
        ListenableFuture<String> result = pool.submit(new ServerNegotiationTask(ServerNegotiationTask.Task.GET_LAST_MOVE, null));
        Futures.addCallback(
            result,
            new FutureCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    String currentMove = ServerNegotiationTask.getGame().getMove();
                    if ("success".equals(response) && currentMove != null && !currentMove.equals(previousMove)) {
                        lastMoveTimer.cancel();
                        ServerNegotiationTask.setFirstMove(false);
                        Platform.runLater( () -> {
                            chessGame.processOpponentsMove(currentMove);  
                            ChessClientApp.setSavedGame(new SavedGame(gamePage, opponent));
                        });
                    }
                    else if ("IOException".equals(response)) {

                    } 
                    else if ("failure".equals(response)) { //opponent ended the game
                    Platform.runLater( () -> {    
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Ended");
                        alert.setContentText("Your opponent has ended the game. Press the End Game button to return to the home page.");
                        alert.showAndWait();    
                    });
        }
                }
                @Override
                public void onFailure(Throwable thrown) {
                    ;
                }
            },
            pool);
    }
    
}
