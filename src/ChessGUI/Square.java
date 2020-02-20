package ChessGUI;

import javafx.geometry.Insets;
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
            color = Color.GREEN;
        else 
            color = Color.CYAN;
        this.rank = rank;
        this.file = file;
        
        this.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
    }
    
    
}
