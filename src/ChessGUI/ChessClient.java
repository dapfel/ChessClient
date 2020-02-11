package ChessGUI;

import ChessGameLogic.SavedGame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class ChessClient extends Application {
    
    private static SavedGame savedGame; // to save/load a game to/from the computer when program not running
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Internet Chess Game");
        ExecutorService pool = Executors.newCachedThreadPool();
        LoginPage loginPage = new LoginPage(primaryStage, pool);
            
        primaryStage.setOnCloseRequest( event -> {   
        pool.shutdown();
        if (savedGame != null)
            saveGame();
        }); 
        
        primaryStage.setScene(loginPage.getLoginScene());
        primaryStage.show();
        loadGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void saveGame() {
        try {
            FileOutputStream fileOut = new FileOutputStream("C:\\Users\\user\\Desktop\\savedGame");
            try (ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(savedGame);
            } 
        } 
        catch (IOException e) {}
    }
    
    public static void loadGame() {
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
}