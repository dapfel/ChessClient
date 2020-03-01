package ChessGUI;

import ChessGameLogic.ChessGame.PlayerColor;
import ChessGameLogic.ChessPiece;
import javafx.scene.layout.GridPane;

/**
 *
 * @author dapfel
 */
public class ChessBoardGUI {

    private final GridPane chessBoardPane;
    private final Square[][] squares;
    private final PlayerColor playerColor;
    
    public ChessBoardGUI(PlayerColor playerColor, ChessPiece[][] board) {
        chessBoardPane = new GridPane();
        squares = new Square[8][8];
        this.playerColor = playerColor;
        
        // for white bottom left is a1. for black bottom left is h8
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int colorType = (x + y) % 2;
                squares[x][y] = new Square(colorType, y + 1, (char) ('a' + x));
                if (playerColor.equals(PlayerColor.WHITE))
                    chessBoardPane.add(squares[x][y], x, 7 - y);
                else
                    chessBoardPane.add(squares[x][y], 7 - x, y);
            }
        }
        initializePieces(board);
    }
    
    private void initializePieces(ChessPiece[][] board) {
        // white pieces
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 8; j++)
                squares[j][i].initializePieceImage(board[j][i]);

        // black pieces
        for (int i = 6; i < 8; i++)
            for (int j = 0; j < 8; j++)
                squares[j][i].initializePieceImage(board[j][i]);
    }
 
    public GridPane getChessBoardPane() {
        return chessBoardPane;
    }
}