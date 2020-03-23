package ChessGameLogic;

import ChessGameLogic.ChessGame.PlayerColor;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author dapfel
 */
public class Pawn extends ChessPiece implements Serializable {
    
    private static boolean promotion;

    public Pawn(ChessGame.PlayerColor color, int rank, char file) {
        super(color, rank, file);
        loadPieceImage();
    }

    @Override
    public boolean isLegalMove(int newRank, char newFile, ChessBoard board) {
        if (this.getColor() == PlayerColor.WHITE) {
            if (file == newFile && (rank == 2 && newRank == 4)) {
                if (board.getPiece(newFile, newRank) == null && board.getPiece(newFile, newRank - 1) == null)
                    return true;
            }
            else if (file == newFile && (rank == newRank - 1)) {
                if (board.getPiece(newFile, newRank) == null) {
                    if (newRank == 8)
                        promotion = true;
                    return true;
                }
            }
            else if (newRank - rank == 1 && Math.abs(newFile - file) == 1) {
                if (!(board.getPiece(newFile, newRank) == null)) {
                    if (newRank == 8)
                        promotion = true;
                    return true;
                }
            }
        }
        else { // its a black pawn
            if (file == newFile && (rank == 7 && newRank == 5)) {
                if (board.getPiece(newFile, newRank) == null && board.getPiece(newFile, newRank + 1) == null)
                    return true;
            }
            else if (file == newFile && (rank == newRank + 1)) {
                if (board.getPiece(newFile, newRank) == null) {
                    if (newRank == 1)
                        promotion = true;
                    return true;
                }
            }
            else if (rank - newRank == 1 && Math.abs(newFile - file) == 1) {
                if (!(board.getPiece(newFile, newRank) == null)) {
                    if (newRank == 8)
                        promotion = true;                     
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPromotion() {
        return promotion;
    }

    public static void setPromotion(boolean promotion) {
        Pawn.promotion = promotion;
    }

    @Override
    public void loadPieceImage() {
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
    
}
