package ChessGUI;

import ChessGameLogic.ServerNegotiationTask;
import java.util.TimerTask;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 *
 * @author dapfel
 */
public class PlayerListsRefresher extends TimerTask {

    private HomePage homePage;
    
    public PlayerListsRefresher(HomePage homePage) {
        this.homePage = homePage;
    }
    
    @Override
    public void run() {
        
        ListView availablePlayersList = homePage.getAvailablePlayersList();
        String[] params = {homePage.getUser().getUsername()};
        try {           
            Future<String> result = homePage.getPool().submit(new ServerNegotiationTask("getAvailableUsers", params));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(result.get());
                Platform.runLater(() -> availablePlayersList.setItems(items));
            }
        }
        catch (Exception e) {} 
            
        ListView requestingPlayersList = homePage.getRequestingPlayersList();
        try {           
            Future<String> result = homePage.getPool().submit(new ServerNegotiationTask("getRequestingUsers", params));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(result.get());
                Platform.runLater(() -> requestingPlayersList.setItems(items));
            }
        }
        catch (Exception e) {}   
    }
}
