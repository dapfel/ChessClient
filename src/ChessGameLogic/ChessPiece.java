package ChessGameLogic;

import ChessGameLogic.ChessGame.PlayerColor;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public abstract class ChessPiece implements Serializable {
    
    final PlayerColor color;
    int rank;
    char file;
    transient BufferedImage image;

    public ChessPiece(PlayerColor color, int rank, char file) {
        this.color = color;
        this.rank = rank;
        this.file = file;
    }
    
    /**
     * @return true if valid isLegalMove, false if not
     */
    public abstract boolean isLegalMove(int newRank, char newFile, ChessBoard board);
    
    public abstract void loadPieceImage();

    public boolean canBlockCheck(King king, ChessBoard board) {
        int currentRank = rank;
        char currentFile = file;
        for (char i = 'a'; i < 'i'; i++)
            for (int j = 1; j < 9; j++)
                if (board.getPiece(i, j) == null && this.isLegalMove(j, i, board)) {
                    this.move(i, j, board);
                    if (!king.inCheck(board)) {  
                        this.move(currentFile, currentRank, board);
                        return true;
                    }
                    this.move(currentFile, currentRank, board);
                }
        return false;
    }   
    
    public void move(char toFile, int toRank, ChessBoard board) {
        board.setPiece(toFile, toRank, this);
        board.setPiece(file, rank, null);
        this.setFile(toFile);
        this.setRank(toRank);
    }
    
    public PlayerColor getColor() {
        return color;
    }

    public int getRank() {
        return rank;
    }

    public char getFile() {
        return file;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setFile(char file) {
        this.file = file;
    }

    public BufferedImage getImage() {
        return image;
    }


}
    
