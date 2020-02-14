
package ChessGameLogic;

/**
 *
 * @author dapfel
 */
public class ChessGame {
    
    public enum PlayerColor {WHITE, BLACK};
    
    private final PlayerColor playerColor;
    
    public ChessGame(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }
    
}
