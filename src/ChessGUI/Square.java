package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessPiece;
import javafx.embed.swing.SwingFXUtils;
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
        PieceImageView pieceImageView = new PieceImageView(piece.getColor(), SwingFXUtils.toFXImage(piece.getImage(), null), piece.getRank(), piece.getFile());
        this.getChildren().add(pieceImageView);
    }
    
    private void setMouseEventHandlers() {
        
        this.setOnMouseEntered(((MouseEvent event) -> {
            this.toFront();
        }));
        
        this.setOnMouseDragReleased(((MouseDragEvent event) -> {  
            PieceImageView newPieceImageView = (PieceImageView) event.getGestureSource();
            if (ChessGame.movePiece(newPieceImageView.getRank(), newPieceImageView.getFile(), this.getRank(), this.getFile())) { // move legality check
                System.out.println("" + file + rank + newPieceImageView.getFile() + newPieceImageView.getRank());
                this.getChildren().clear(); 
                this.getChildren().add(newPieceImageView);  
                resetPieceImage(newPieceImageView, event);
                event.consume();
                }
        }));
    }

    public int getRank() {
        return rank;
    }

    public char getFile() {
        return file;
    }

    private void resetPieceImage(PieceImageView newPieceImageView, MouseDragEvent event) {
        newPieceImageView.setFile(this.getFile());
        newPieceImageView.setRank(this.getRank()); 
        newPieceImageView.setTranslateX(0);
        newPieceImageView.setTranslateY(0);
        newPieceImageView.setStartDragX(event.getSceneX());
        newPieceImageView.setStartDragY(event.getSceneY());
        newPieceImageView.setMouseTransparent(false);
    }
}
