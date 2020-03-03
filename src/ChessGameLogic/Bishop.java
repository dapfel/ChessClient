package ChessGameLogic;

import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Bishop extends ChessPiece {

    public Bishop(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
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

    @Override
    public boolean move(int newRank, char newFile, ChessPiece[][] board) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
