package ChessGameLogic;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Rook extends ChessPiece {

    public Rook(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
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
    public boolean move(int newRank, char newFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
