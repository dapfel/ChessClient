package ChessGUI;

import ChessGameLogic.SavedGame;
import ServerAccess.ServerNegotiationTask;
import ServerAccess.ServerNegotiationTask.Task;
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
    
    public enum Page {LOGIN, REGISTER, HOME, UPDATE_USER, GAME}
    
    private static SavedGame savedGame; // to save/load a game to/from the computer when program not running
    private static ListeningExecutorService pool;
    private static Stage primaryStage;
    private static Page currentPage;
    
    @Override
    public void start(Stage primaryStage) {
        ChessClientApp.primaryStage = primaryStage;       
        pool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        
        ProgressDialog.setPrimaryStage(primaryStage);
        LoginPage loginPage = new LoginPage();
        primaryStage.setTitle("Internet Chess Game");    
        
        primaryStage.setOnCloseRequest( event -> {   
        setUnavailable();
        if (currentPage == Page.HOME) {
            HomePage.stopRefreshTimers();
        }
        if (currentPage == Page.GAME) {
             saveGame();  
        }
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
            FileOutputStream fileOut = new FileOutputStream("/SavedGame");
            try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(savedGame);
            } 
        } 
        catch (IOException e) {}
    }
    
    private static void loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream("/SavedGame");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            savedGame = (SavedGame) objectIn.readObject();
        } 
        catch (IOException | ClassNotFoundException e) {}
    }
    
    private void setUnavailable() {
        String[] params = {"unavailable"};
        ServerNegotiationTask task = new ServerNegotiationTask(Task.RESET, params);
        pool.submit(task);
    }

    public static ListeningExecutorService getPool() {
        return pool;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setCurrentPage(Page currentPage) {
        ChessClientApp.currentPage = currentPage;
    }
    
    public static SavedGame getSavedGame() {
        return savedGame;
    }
}