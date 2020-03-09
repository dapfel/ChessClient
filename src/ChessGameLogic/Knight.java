package ChessGameLogic;

import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Knight extends ChessPiece {

    public Knight(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        try {
            if (color == ChessGame.PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_knight.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_knight.png"));
            }
        }
        catch (IOException e) {
            
        }
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        if (Math.abs(rank - newRank) == 1 && Math.abs(file - newFile) == 2)
            return true;
        if (Math.abs(rank - newRank) == 2 && Math.abs(file - newFile) == 1)
            return true;
        else
            return false;
    }
        
}
