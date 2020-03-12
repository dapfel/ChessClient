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
    
