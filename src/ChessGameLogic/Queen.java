package ChessGameLogic;

import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Queen extends ChessPiece implements Serializable {

    public Queen(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        loadPieceImage();
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        //cheat by treating queen as a combined rook and bishop
        Rook rook = new Rook(this.getColor(), this.getRank(), this.getFile());
        Bishop bishop = new Bishop(this.getColor(), this.getRank(), this.getFile());
        if (rook.isLegalMove(newRank, newFile, board) || bishop.isLegalMove(newRank, newFile, board))
            return true;
        else
            return false;
    }

    @Override
    public void loadPieceImage() {
        try {
            if (color == ChessGame.PlayerColor.WHITE) {
                image = ImageIO.read(getClass().getResource("/Images/white_queen.png"));
            }
            else {
                image = ImageIO.read(getClass().getResource("/Images/black_queen.png"));
            }
        }
        catch (IOException e) {
            
        }
    }
        
}
