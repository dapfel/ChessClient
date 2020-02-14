package ChessGUI;

import ChessGameLogic.ChessGame.PlayerColor;
import javafx.scene.layout.GridPane;

/**
 *
 * @author dapfel
 */
public class ChessBoardPane extends GridPane {

    private final Square[][] squares;
    private final PlayerColor playerColor;
    
    public ChessBoardPane(PlayerColor playerColor) {
        super();
        squares = new Square[8][8];
        this.playerColor = playerColor;
        
        // for white bottom left is a1. for black bottom left is h8
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                int colorType = (x + y) % 2;
                squares[x][y] = new Square(colorType, x + 1, (char) ('a' + y));
                if (playerColor.equals(PlayerColor.WHITE))
                    this.add(squares[x][y], y, 7 - x);
                else
                    this.add(squares[x][y], 7 - y, x);
            }
    }
    
    
    
}
