package ChessGameLogic;

import ChessGameLogic.ChessGame.PlayerColor;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Pawn extends ChessPiece {

    public Pawn(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        try {
            if (color == PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_pawn.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_pawn.png"));
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
