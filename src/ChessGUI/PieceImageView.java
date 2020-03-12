package ChessGUI;

import ChessGameLogic.Bishop;
import ChessGameLogic.ChessGame.PlayerColor;
import ChessGameLogic.ChessPiece;
import ChessGameLogic.King;
import ChessGameLogic.Knight;
import ChessGameLogic.Queen;
import ChessGameLogic.Rook;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author dapfel
 */
public class PieceImageView extends ImageView {
    
    public enum PieceType {KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN}
    
    private final PlayerColor color;
    private final PieceType type;
    private int rank;
    private char file;
    private double startDragX;
    private double startDragY;
    
    public PieceImageView(ChessPiece piece) {
        super(SwingFXUtils.toFXImage(piece.getImage(), null));
        if (piece.getClass().equals(Queen.class))
            type = PieceType.QUEEN;
        else if (piece.getClass().equals(King.class))
            type = PieceType.KING;
        else if (piece.getClass().equals(Rook.class))
            type = PieceType.ROOK;
        else if (piece.getClass().equals(Knight.class))
            type = PieceType.KNIGHT;
        else if (piece.getClass().equals(Bishop.class))
            type = PieceType.BISHOP;
        else // (piece.getClass().equals(Pawn.class))
            type = PieceType.PAWN;
        
        color = piece.getColor();       
        rank = piece.getRank();
        file = piece.getFile();
    }
    
    public void setMouseEventHandlers() {
        this.setOnMousePressed((MouseEvent event) -> {
            this.setMouseTransparent(true);
            event.consume();
        });
            
        this.setOnDragDetected((MouseEvent event) -> {
            startDragX = event.getSceneX();
            startDragY = event.getSceneY();
            this.startFullDrag();
            event.consume();
            
        });

        this.setOnMouseDragged((MouseEvent event) -> {
            this.setTranslateX(event.getSceneX() - startDragX);
            this.setTranslateY(event.getSceneY() - startDragY);
            event.consume();
        });
    }
    
    public PlayerColor getColor() {
        return color;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public char getFile() {
        return file;
    }

    public void setFile(char file) {
        this.file = file;
    }

    public void setStartDragX(double startDragX) {
        this.startDragX = startDragX;
    }

    public void setStartDragY(double startDragY) {
        this.startDragY = startDragY;
    }

    public PieceType getType() {
        return type;
    }
}
