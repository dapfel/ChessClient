package ChessGameLogic;

import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Bishop extends ChessPiece implements Serializable {

    public Bishop(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        loadPieceImage();
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        if (Math.abs(newRank - rank) == Math.abs(newFile - file)) {
            int xDirection;
            int yDirection;
            if (newRank > rank && newFile < file) { // bishop going towards upper left
                xDirection = -1;
                yDirection = 1;
            }
            else if (newRank > rank && newFile > file) { // bishop going towards upper right
                xDirection = 1;
                yDirection = 1;
            }
            else if (newRank < rank && newFile < file) { // bishop going towards lower left
                xDirection = -1;
                yDirection = -1;
            }
            else { // bishop going towards lower right
                xDirection = 1;
                yDirection = -1;
            }
            for (int i = 1; i < Math.abs(newRank - rank); i++) {
                if (board.getPiece((char) (file + i*xDirection), rank + i*yDirection) != null)
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void loadPieceImage() {
        try {
            if (color == ChessGame.PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_bishop.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_bishop.png"));
            }
        }
        catch (IOException e) {
            
        }
    }
        
}
