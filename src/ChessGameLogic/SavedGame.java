package ChessGameLogic;

import ChessGUI.GamePage;
import ServerAccess.Game;
import ServerAccess.ServerNegotiationTask;
import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public class SavedGame implements Serializable {
    
    private final GamePage gamePage;
    private final Game game;
    private final String opponent;

    public SavedGame(GamePage gamePage, String opponent) {
        this.gamePage = gamePage;
        this.game = ServerNegotiationTask.getGame();
        this.opponent = opponent;
    }

    public GamePage getGamePage() {
        return gamePage;
    }

    public Game getGame() {
        return game;
    }

    public String getOpponent() {
        return opponent;
    }
    
}
