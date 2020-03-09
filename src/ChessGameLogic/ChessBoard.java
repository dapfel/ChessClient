package ChessGameLogic;

/**
 *
 * @author dapfel
 */
public class ChessBoard {
    
    private final ChessPiece[][] board;
    
    public ChessBoard() {
        board = new ChessPiece[8][8];
        initializePieces();
    }
    
    private void initializePieces() {
        board[0][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'a');
        board[1][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'b');
        board[2][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'c');
        board[3][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'd');
        board[4][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'e');
        board[5][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'f');
        board[6][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'g');
        board[7][1] = new Pawn(ChessGame.PlayerColor.WHITE, 2, 'h');
        board[0][0] = new Rook(ChessGame.PlayerColor.WHITE, 1, 'a');
        board[7][0] = new Rook(ChessGame.PlayerColor.WHITE, 1, 'h');
        board[1][0] = new Knight(ChessGame.PlayerColor.WHITE, 1, 'b');
        board[6][0] = new Knight(ChessGame.PlayerColor.WHITE, 1, 'g');
        board[2][0] = new Bishop(ChessGame.PlayerColor.WHITE, 1, 'c');
        board[5][0] = new Bishop(ChessGame.PlayerColor.WHITE, 1, 'f');
        board[3][0] = new Queen(ChessGame.PlayerColor.WHITE, 1, 'd');
        board[4][0] = new King(ChessGame.PlayerColor.WHITE, 1, 'e');
        
        board[0][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'a');
        board[1][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'b');
        board[2][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'c');
        board[3][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'd');
        board[4][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'e');
        board[5][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'f');
        board[6][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'g');
        board[7][6] = new Pawn(ChessGame.PlayerColor.BLACK, 7, 'h');
        board[0][7] = new Rook(ChessGame.PlayerColor.BLACK, 8, 'a');
        board[7][7] = new Rook(ChessGame.PlayerColor.BLACK, 8, 'h');
        board[1][7] = new Knight(ChessGame.PlayerColor.BLACK, 8, 'b');
        board[6][7] = new Knight(ChessGame.PlayerColor.BLACK, 8, 'g');
        board[2][7] = new Bishop(ChessGame.PlayerColor.BLACK, 8, 'c');
        board[5][7] = new Bishop(ChessGame.PlayerColor.BLACK, 8, 'f');
        board[3][7] = new Queen(ChessGame.PlayerColor.BLACK, 8, 'd');
        board[4][7] = new King(ChessGame.PlayerColor.BLACK, 8, 'e');
    }
    
    public ChessPiece getPiece(char file, int rank) {
        return board[file - 'h' + 7][rank - 1];
    }
    
    public void setPiece(char file, int rank, ChessPiece piece) {
        board[file - 'h' + 7][rank - 1] = piece;
    }
}
