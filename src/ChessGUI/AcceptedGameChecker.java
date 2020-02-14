package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ServerNegotiationTask;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
import ChessGameLogic.ChessGame.PlayerColor;

/**
 *
 * @author dapfel
 */
public class AcceptedGameChecker extends TimerTask {

    private final HomePage homePage;
    
    public AcceptedGameChecker(HomePage homePage) {
        this.homePage = homePage;
    }
    
    @Override
    public void run() {
        
        try {
            ServerNegotiationTask checkForAcceptedTask = new ServerNegotiationTask("whiteStartGame", new String[0]);
            Future<String> result = homePage.getPool().submit(checkForAcceptedTask);
        
            if (result.get().equals("success")) {
                homePage.stopRefreshTimers();
                GamePage gamePage = new GamePage(homePage.getPrimaryStage(), homePage.getPool(), new ChessGame(PlayerColor.WHITE));
                Platform.runLater(() -> homePage.getPrimaryStage().setScene(gamePage.getGameScene()));
            }
            if (result.get().equals("IOException")) {
                Platform.runLater(() -> homePage.reset("onlyAvailable"));
            }
        }
        catch (InterruptedException | ExecutionException e) {}
    }
    
}
