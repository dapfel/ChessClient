
package ChessGameLogic;

import ChessGUI.ChessBoardGUI;

/**
 *
 * @author dapfel
 */
public class ChessGame {
    
    public enum PlayerColor {WHITE, BLACK};
    
    private static PlayerColor playerColor; // color of the client player
    private PlayerColor turn; // whoose turn it is 
    private final ChessPiece[][] board;
    private final ChessBoardGUI boardGUI;
   
    
    public ChessGame(PlayerColor playerColor) {
        ChessGame.playerColor = playerColor;
        board = new ChessPiece[8][8];        
        initializePieces();        
        boardGUI = new ChessBoardGUI(playerColor, board);
        turn = PlayerColor.WHITE;
    }

    private void initializePieces() {
        board[0][1] = new Pawn(PlayerColor.WHITE, 2, 'a');
        board[1][1] = new Pawn(PlayerColor.WHITE, 2, 'b');
        board[2][1] = new Pawn(PlayerColor.WHITE, 2, 'c');
        board[3][1] = new Pawn(PlayerColor.WHITE, 2, 'd');
        board[4][1] = new Pawn(PlayerColor.WHITE, 2, 'e');
        board[5][1] = new Pawn(PlayerColor.WHITE, 2, 'f');
        board[6][1] = new Pawn(PlayerColor.WHITE, 2, 'g');
        board[7][1] = new Pawn(PlayerColor.WHITE, 2, 'h');
        board[0][0] = new Rook(PlayerColor.WHITE, 1, 'a');
        board[7][0] = new Rook(PlayerColor.WHITE, 1, 'h');
        board[1][0] = new Knight(PlayerColor.WHITE, 1, 'b');
        board[6][0] = new Knight(PlayerColor.WHITE, 1, 'g');
        board[2][0] = new Bishop(PlayerColor.WHITE, 1, 'c');
        board[5][0] = new Bishop(PlayerColor.WHITE, 1, 'f');
        board[3][0] = new Queen(PlayerColor.WHITE, 1, 'd');
        board[4][0] = new King(PlayerColor.WHITE, 1, 'e');
        
        board[0][6] = new Pawn(PlayerColor.BLACK, 7, 'a');
        board[1][6] = new Pawn(PlayerColor.BLACK, 7, 'b');
        board[2][6] = new Pawn(PlayerColor.BLACK, 7, 'c');
        board[3][6] = new Pawn(PlayerColor.BLACK, 7, 'd');
        board[4][6] = new Pawn(PlayerColor.BLACK, 7, 'e');
        board[5][6] = new Pawn(PlayerColor.BLACK, 7, 'f');
        board[6][6] = new Pawn(PlayerColor.BLACK, 7, 'g');
        board[7][6] = new Pawn(PlayerColor.BLACK, 7, 'h');
        board[0][7] = new Rook(PlayerColor.BLACK, 8, 'a');
        board[7][7] = new Rook(PlayerColor.BLACK, 8, 'h');
        board[1][7] = new Knight(PlayerColor.BLACK, 8, 'b');
        board[6][7] = new Knight(PlayerColor.BLACK, 8, 'g');
        board[2][7] = new Bishop(PlayerColor.BLACK, 8, 'c');
        board[5][7] = new Bishop(PlayerColor.BLACK, 8, 'f');
        board[4][7] = new Queen(PlayerColor.BLACK, 8, 'e');
        board[3][7] = new King(PlayerColor.BLACK, 8, 'd');
    }
    
    public static boolean movePiece(int fromRank, char fromFile, int toRank, char toFile) {
                //in bounds (cant not be already). if same square - dont change turn. if taking piece of own color - return false.
        // set pieces rank and file to new rank and file if good move
        //in spacific piece move do stuff thats only for that piece
        return true;
    }
      
    public static PlayerColor getPlayerColor() {
        return playerColor;
    }

    public ChessBoardGUI getBoardGUI() {
        return boardGUI;
    }
    
}
