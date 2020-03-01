package ChessGameLogic;

import ServerAccess.Game;
import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public class SavedGame implements Serializable {
    
    private final ChessGame chessGame;
    private final Game game;
    private final String opponent;

    public SavedGame(ChessGame chessGame, Game game, String opponent) {
        this.chessGame = chessGame;
        this.game = game;
        this.opponent = opponent;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    public Game getGame() {
        return game;
    }

    public String getOpponent() {
        return opponent;
    }
    
}
