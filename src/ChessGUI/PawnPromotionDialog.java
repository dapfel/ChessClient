package ChessGUI;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ChoiceDialog;

/**
 *
 * @author dapfel
 */
public class PawnPromotionDialog {
    
    private ChoiceDialog promotionDialog;
    
    public PawnPromotionDialog() {
        List<String> choices = new ArrayList<>();
        choices.add("Queen");
        choices.add("Rook");
        choices.add("Knight");
        choices.add("Bishop");
        
        promotionDialog = new ChoiceDialog("Queen", choices);

        promotionDialog.setTitle("Pawn Promotion");
        promotionDialog.setHeaderText("Choose a piece to promote your Pawn to");
        promotionDialog.setContentText("Choose your piece:");
    }

    public ChoiceDialog getPromotionDialog() {
        return promotionDialog;
    }
    
}
