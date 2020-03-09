package ChessGameLogic;

import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Rook extends ChessPiece {
    
    private boolean hasBeenMoved;

    public Rook(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        hasBeenMoved = false;
        try {
            if (color == ChessGame.PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_rook.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_rook.png"));
            }
        }
        catch (IOException e) {
            
        }
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        if (rank == newRank || file == newFile) {
            int xDirection;
            int yDirection;
            if (newRank > rank) { // rook going up
                xDirection = 0;
                yDirection = 1;
            }
            else if (newRank < rank) { // rook going down
                xDirection = 0;
                yDirection = -1;
            }
            else if (newFile < file) { // rook going to left
                xDirection = -1;
                yDirection = 0;
            }
            else { // rook going right
                xDirection = 1;
                yDirection = 0;
            }
            for (int i = 1; i < Math.abs(newRank - rank + newFile - file); i++) {
                if (board.getPiece((char) (file + i*xDirection), rank + i*yDirection) != null)
                    return false;
            }
            return true;            
        }
        return false;
    }

    public boolean hasBeenMoved() {
        return hasBeenMoved;
    }

    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }
       
    
}
