package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ServerNegotiationTask;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;

/**
 *
 * @author dapfel
 */
public class AcceptedGameChecker extends TimerTask {

    private HomePage homePage;
    
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
                String opponent = ServerNegotiationTask.getGameRequest().getGamerequestPK().getRequestedUser();
                GamePage gamePage = new GamePage(homePage.getPrimaryStage(), homePage.getPool(), new ChessGame());
                Platform.runLater(() -> homePage.getPrimaryStage().setScene(gamePage.getGameScene()));
            }
        }
        catch (InterruptedException | ExecutionException e) {}
    }
    
}
