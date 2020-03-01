package ChessGUI;

import ChessGameLogic.ChessGame;
import ChessGameLogic.ChessGame.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author dapfel
 */
public class PieceImageView extends ImageView {
    
    private final PlayerColor color;
    private int rank;
    private char file;
    private double startDragX;
    private double startDragY;

    public PieceImageView(PlayerColor color, Image image, int rank, char file) {
        super(image);
        this.color = color;
        this.rank = rank;
        this.file = file;
        if (color.equals(ChessGame.getPlayerColor()))
            setMouseEventHandlers();
    }
    
    private void setMouseEventHandlers() {
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
  
}
