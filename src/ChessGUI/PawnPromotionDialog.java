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
        
        ChoiceDialog<String> choiceDialog = new ChoiceDialog("Queen", choices);

        choiceDialog.setTitle("Pawn Promotion");
        choiceDialog.setHeaderText("Choose a piece to promote your Pawn to");
        choiceDialog.setContentText("Choose your piece:");
    }

    public ChoiceDialog getPromotionDialog() {
        return promotionDialog;
    }
    
}
