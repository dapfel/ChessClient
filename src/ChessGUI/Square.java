package ChessGUI;

import ChessGameLogic.ChessPiece;
import javafx.geometry.Insets;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author dapfel
 */
public class Square extends StackPane {
    
    private final int rank;
    private final char file;
    private static ChessBoardGUI chessBoardGUI;

    public Square(int colorType, int rank, char file) {
        super();
        
        Color color;
        if (colorType == 0)
            color = Color.DARKGREEN;
        else 
            color = Color.BEIGE;
        this.rank = rank;
        this.file = file;
        
        this.setPickOnBounds(false);
        this.setPrefSize(60, 60);
        this.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        setMouseEventHandlers();
    }

    public void initializePieceImage(ChessPiece piece) {
        PieceImageView pieceImageView = new PieceImageView(piece);
        // only set mouse event handlers for pieceImageView if its this players piece
        if (chessBoardGUI.getChessGame().getPlayerColor().equals(pieceImageView.getColor()))
            pieceImageView.setMouseEventHandlers();
        this.getChildren().add(pieceImageView);
    }
    
    private void setMouseEventHandlers() {
        
        this.setOnMouseEntered(((MouseEvent event) -> {
            this.toFront();
        }));
        
        this.setOnMouseDragReleased(((MouseDragEvent event) -> {  
            PieceImageView newPieceImageView = (PieceImageView) event.getGestureSource();
            chessBoardGUI.ProcessPlayersMove(this, newPieceImageView);
            event.consume();
        }));
    }

    public int getRank() {
        return rank;
    }

    public char getFile() {
        return file;
    }

    public static void setChessBoardGUI(ChessBoardGUI chessBoardGUI) {
        Square.chessBoardGUI = chessBoardGUI;
    }
}
