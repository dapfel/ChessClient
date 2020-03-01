package ChessGUI;

import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
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

    private final HomePage homePage;
    
    public PlayerListsRefresher(HomePage homePage) {
        this.homePage = homePage;
    }
    
    @Override
    public void run() {
        
        ListView availablePlayersList = homePage.getAvailablePlayersList();
        String[] params = {homePage.getUser().getUsername()};
        try {           
            Future<String> result = homePage.getPool().submit(new ServerNegotiationTask(Task.GET_AVAILABLE_USERS, params));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(ServerNegotiationTask.getAvailableUsers());
                Platform.runLater(() -> availablePlayersList.setItems(items));
            }
        }
        catch (InterruptedException | ExecutionException e) {} 
            
        ListView requestingPlayersList = homePage.getRequestingPlayersList();
        try {           
            Future<String> result = homePage.getPool().submit(new ServerNegotiationTask(Task.GET_REQUESTING_USERS, params));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(ServerNegotiationTask.getRequestingUsers());
                Platform.runLater(() -> requestingPlayersList.setItems(items));
            }
        }
        catch (InterruptedException | ExecutionException e) {}   
    }
}
