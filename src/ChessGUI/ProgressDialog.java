package ChessGUI;

import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author dapfel
 */
public class ProgressDialog extends Stage {
    
    private static Stage primaryStage;
     
    public ProgressDialog(String title) {
        super();
        StackPane progressPane = new StackPane();      
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.setPrefSize(350, 40);
	this.setTitle(title);
	this.initModality(Modality.WINDOW_MODAL);
        progressPane.getChildren().add(progressBar);
        this.initOwner(primaryStage);
        
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth()/2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight()/2d;
        this.setX(centerXPosition);
        this.setY(centerYPosition);	
        
	Scene progressScene = new Scene(progressPane, 400, 50);		
        this.setScene(progressScene);
    }		

    public static void setPrimaryStage(Stage primaryStage) {
        ProgressDialog.primaryStage = primaryStage;
    }
    
}