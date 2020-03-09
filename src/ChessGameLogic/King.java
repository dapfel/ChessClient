package ChessGameLogic;

import ChessGameLogic.ChessGame.PlayerColor;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class King extends ChessPiece {
    
    private boolean hasBeenMoved;
    private static boolean castle;

    public King(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        hasBeenMoved = false;
        try {
            if (color == ChessGame.PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_king.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_king.png"));
            }
        }
        catch (IOException e) {
            
        }
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        
        if ((rank == newRank || Math.abs(rank - newRank) == 1) && (file == newFile || Math.abs(file - newFile) == 1))
            return true;
        
        //--------- castling ----------------------------------
        if (this.hasBeenMoved || inCheck(board))
            return false;
        // white player
        if (this.getColor().equals(PlayerColor.WHITE)) {
            if (newRank == 1 && newFile == 'g') {// king side castle
                ChessPiece potentialRook = board.getPiece('h', 1);
                if (potentialRook != null && potentialRook.getClass().equals(Rook.class)) {
                    Rook rook = (Rook) potentialRook;
                    if (!rook.hasBeenMoved()) {
                        //check that in between spaces are empty and not castling through a check
                        if (board.getPiece('f', 1) != null || board.getPiece('g', 1) != null)
                            return false;
                        King king = (King) board.getPiece('e',1);
                        board.setPiece('e',1 , null);
                        board.setPiece('f', 1, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('f', 1, null);
                        board.setPiece('g', 1, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('g', 1, null);
                        board.setPiece('e', 1, king);
                        castle = true;
                        return true;
                    }
                }
            }
            if (newRank == 1 && newFile == 'c') {// queen side castle
                ChessPiece potentialRook = board.getPiece('a', 1);
                if (potentialRook != null && potentialRook.getClass().equals(Rook.class)) {
                    Rook rook = (Rook) potentialRook;
                    if (!rook.hasBeenMoved()) {
                        //check that in between spaces are empty and not castling through a check
                        if (board.getPiece('a', 1) != null || board.getPiece('b', 1) != null || board.getPiece('c', 1) != null)
                            return false;
                        King king = (King) board.getPiece('d', 1);
                        board.setPiece('d', 1, null);
                        board.setPiece('c', 1, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('c', 1, null);
                        board.setPiece('b', 1, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('b', 1, null);
                        board.setPiece('a', 1, king);
                        if (inCheck(board))
                            return false;     
                        board.setPiece('a', 1, null);
                        board.setPiece('d', 1, king);                        
                        castle = true;
                        return true;
                    }
                }
            }
        }  
        // black player
        else {
            if (newRank == 8 && newFile == 'g') {// king side castle
                ChessPiece potentialRook = board.getPiece('h', 8);
                if (potentialRook != null && potentialRook.getClass().equals(Rook.class)) {
                    Rook rook = (Rook) potentialRook;
                    if (!rook.hasBeenMoved()) {
                        //check that in between spaces are empty and not castling through a check
                        if (board.getPiece('f', 8) != null || board.getPiece('g', 8) != null)
                            return false;
                        King king = (King) board.getPiece('e', 8);
                        board.setPiece('e', 8, null);
                        board.setPiece('f', 8, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('f', 8, null);
                        board.setPiece('g', 8, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('g', 8, null);
                        board.setPiece('e', 8, king);
                        castle = true;
                        return true;
                    }
                }
            }
            if (newRank == 8 && newFile == 'c') {// queen side castle
                ChessPiece potentialRook = board.getPiece('a', 8);
                if (potentialRook != null && potentialRook.getClass().equals(Rook.class)) {
                    Rook rook = (Rook) potentialRook;
                    if (!rook.hasBeenMoved()) {
                        //check that in between spaces are empty and not castling through a check
                        if (board.getPiece('b', 8) != null || board.getPiece('c', 8) != null || board.getPiece('d', 8) != null)
                            return false;
                        King king = (King) board.getPiece('e', 8);
                        board.setPiece('e', 8, null);
                        board.setPiece('d', 8, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('d', 8, null);
                        board.setPiece('c', 8, king);
                        if (inCheck(board))
                            return false;
                        board.setPiece('c', 8, null);
                        board.setPiece('b', 8, king);
                        if (inCheck(board))
                            return false;     
                        board.setPiece('b', 8, null);
                        board.setPiece('e', 8, king);     
                        castle = true;
                        return true;
                    }
                }
            }            
        }

        return false;
    }
    
    public boolean inCheck(ChessBoard board) {      
        for (char i = 'a'; i < 'i'; i++)
            for (int j = 1; j < 9; j++)
                if (board.getPiece(i,j) != null && !board.getPiece(i,j).getColor().equals(this.getColor())) {
                    if (board.getPiece(i,j).getClass().equals(King.class)) {  // have to check king seperately so as not to get into infinite loop - 
                        if (adjacent(this, board.getPiece(i,j)))             // isLegalMove of king calls inCheck
                            return true;     
                    }
                    else if (board.getPiece(i,j).isLegalMove(rank, file, board))
                        return true;
                }
        return false;
    }
    
    public boolean inCheckmate(ChessBoard board) {
        if (!inCheck(board))
            return false;
        
        int currentRank = rank; 
        char currentFile = file;
        
        // check if king can move
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (currentRank + i > 0 && currentRank + i < 9 && currentFile + j > 'a' && currentFile + j < 'h') {
                     moveKing(currentRank + i, (char) (currentFile + j), board);
                     if (!inCheck(board))
                         return false;
                }      
        moveKing(currentRank, currentFile, board); 
        
        //get attacking piece
        ChessPiece attackingPiece = null;      
        for (char i = 'a'; i < 'i'; i++)
            for (int j = 1; j < 9; j++)
                if (board.getPiece(i, j) != null && !board.getPiece(i, j).getColor().equals(this.getColor()))
                    if (board.getPiece(i, j).isLegalMove(rank, file, board))
                        attackingPiece = board.getPiece(i, j);
        
        // check if any piece can take the attacker
        for (char i = 'a'; i < 'i'; i++)
            for (int j = 1; j < 9; j++)
                if (board.getPiece(i, j) != null && board.getPiece(i, j).getColor().equals(this.getColor()))
                    if (board.getPiece(i, j).isLegalMove(attackingPiece.getRank(), attackingPiece.getFile(), board))
                        return false;
        return true;
    }
    
    private void moveKing(int toRank, char toFile, ChessBoard board) {
        board.setPiece(toFile, toRank, board.getPiece(file, rank));
        board.setPiece(file, rank, null);
        board.getPiece(toFile, toRank).setFile(toFile);
        board.getPiece(toFile, toRank).setRank(toRank);
    }
    
    private boolean adjacent(ChessPiece piece1, ChessPiece piece2) {
        return (Math.abs(piece1.getFile() - piece2.getFile()) < 2 && Math.abs(piece1.getRank() - piece2.getRank()) < 2);
    }

    public static boolean isCastle() {
        return castle;
    }

    public static void setCastle(boolean castle) {
        King.castle = castle;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }
        
    
}
