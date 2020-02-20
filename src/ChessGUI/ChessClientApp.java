package ChessGUI;

import ChessGameLogic.SavedGame;
import ChessGameLogic.ServerNegotiationTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class ChessClientApp extends Application {
    
    private static SavedGame savedGame; // to save/load a game to/from the computer when program not running
    private ListeningExecutorService pool;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Internet Chess Game");
        pool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        ProgressDialog.setPrimaryStage(primaryStage);
        LoginPage loginPage = new LoginPage(primaryStage, pool);
            
        primaryStage.setOnCloseRequest( event -> {   
        if (savedGame != null)
            saveGame();
        setUnavailable();
        HomePage.stopRefreshTimers();
        pool.shutdown();
        }); 
        
        primaryStage.setScene(loginPage.getLoginScene());
        primaryStage.show();
        loadGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private static void saveGame() {
        try {
            FileOutputStream fileOut = new FileOutputStream("C:\\Users\\user\\Desktop\\savedGame");
            try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(savedGame);
            } 
        } 
        catch (IOException e) {}
    }
    
    private static void loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream("C:\\Users\\user\\Desktop\\savedGame");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            savedGame = (SavedGame) objectIn.readObject();
        } 
        catch (IOException | ClassNotFoundException e) {}
    }

    public static SavedGame getSavedGame() {
        return savedGame;
    }
    
    private void setUnavailable() {
        String[] params = {"unavailable"};
        ServerNegotiationTask task = new ServerNegotiationTask("reset", params);
        pool.submit(task);
    }
}