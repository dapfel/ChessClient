package ChessGUI;

import ChessGameLogic.ServerNegotiationTask;
import ServerAccess.User;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author dapfel
 */
public class HomePage {
    private final Scene homeScene;
    private User user;
    private ListView<String> availablePlayersList;
    private ListView<String> requestingPlayersList;
    private final Stage primaryStage;
    private final ExecutorService pool;
    PlayerListsRefresher playerListsRefresher;
    AcceptedGameChecker acceptedGameChecker;
    Timer timer1;
    Timer timer2;
    
    public HomePage(Stage primaryStage, ExecutorService pool, User user) {
        
        this.user = user;
        this.primaryStage = primaryStage;
        this.pool = pool;
        reset("available"); // reset to no games or requests for the client user and that available
        
        BorderPane border = new BorderPane();
        border.setTop(TopButtonsHbox());
        border.setLeft(UserDetailsVbox());
        border.setCenter(AvailablePlayersList());
        border.setRight(RequestingPlayersList());
        border.setBottom(BottomButtonsHbox());
        
        homeScene = new Scene(border, 600, 300);
    }
    
    private HBox TopButtonsHbox() {
        HBox topButtonsHbox = new HBox();
        topButtonsHbox.setPadding(new Insets(15, 12, 15, 12));
        topButtonsHbox.setSpacing(10);

        Button logOutButton = new Button("Log Out");
        logOutButton.setPrefSize(100, 20);
        logOutButton.setOnMouseClicked(mouseEvent -> {
            user = null;
            reset("unavailable");
            Scene loginScene = new LoginPage(primaryStage, pool).getLoginScene();
            primaryStage.setScene(loginScene);
            primaryStage.show();
        });
        
        Button updateUserButton = new Button("Update User Profile");
        updateUserButton.setPrefSize(100, 20);
        updateUserButton.setOnMouseClicked(mouseEvent -> {
            Scene updateUserScene = new UpdateUserPage(primaryStage, pool).getUpdateUserScene();
            primaryStage.setScene(updateUserScene);
            primaryStage.show();
        });
        
        topButtonsHbox.getChildren().addAll(logOutButton,updateUserButton);
        return topButtonsHbox;
   }
    
    private VBox UserDetailsVbox() {
        VBox userDetailsVbox = new VBox();
        userDetailsVbox.setPadding(new Insets(10));
        userDetailsVbox.setSpacing(8);

        Text title = new Text(user.getUsername());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        userDetailsVbox.getChildren().add(title);

        Label userDetails[] = new Label[] {
        new Label("Wins - " + user.getWins()),
        new Label("Losses - " + user.getLosses()),
        new Label("Draws - " + user.getDraws())};

        for (Label label : userDetails) {
            VBox.setMargin(label, new Insets(0, 0, 0, 8));
            userDetailsVbox.getChildren().add(label);
        }

        return userDetailsVbox;
    }
    
    private ListView<String> AvailablePlayersList() {
        availablePlayersList = new ListView<>();
        availablePlayersList.setPrefWidth(70);
        availablePlayersList.setPrefHeight(150);
        availablePlayersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        try {           
            Future<String> result = pool.submit(new ServerNegotiationTask("getAvailableUsers", new String[0]));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(result.get());
                availablePlayersList.setItems(items);
            }
        }
        catch (InterruptedException | ExecutionException e) {}      
        
        return availablePlayersList;
    }
    
    private ListView<String> RequestingPlayersList() {
        requestingPlayersList = new ListView<>();
        requestingPlayersList.setPrefWidth(70);
        requestingPlayersList.setPrefHeight(150);
        requestingPlayersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        try {           
            Future<String> result = pool.submit(new ServerNegotiationTask("getRequestingUsers", new String[0]));
            if (result.get().equals("success")) {
                ObservableList<String> items = FXCollections.observableArrayList(result.get());
                requestingPlayersList.setItems(items);
            }
        }
        catch (InterruptedException | ExecutionException e) {}      
        
        return requestingPlayersList;
    }
    
    private HBox BottomButtonsHbox() {
        HBox bottomButtonsHbox = new HBox();
        bottomButtonsHbox.setPadding(new Insets(15, 12, 15, 12));
        bottomButtonsHbox.setSpacing(10);
        
        Button requestGameButton = new Button("Request Game");
        requestGameButton.setPrefSize(100, 20);
        requestGameButton.setOnMouseClicked(mouseEvent -> {
            String[] params = {availablePlayersList.getSelectionModel().getSelectedItem()};
            Future<String> result = pool.submit(new ServerNegotiationTask("requestGame", params));
            Alert alert;
            try {
                if (result.get().equals("success")) {
                    availablePlayersList.getItems().remove(params[0]);                 
                }
                else {//IOException
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error connecting to Server. Please try again.");
                    alert.showAndWait();
                }
            }
            catch (InterruptedException | ExecutionException e) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Execution Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occured in proccessing your request. Please try again.");
                    alert.showAndWait();                 
            }
        });

        Button acceptRequestButton = new Button("Accept Request");
        acceptRequestButton.setPrefSize(100, 20);
        acceptRequestButton.setOnMouseClicked(mouseEvent -> {
            stopRefreshTimers();
            String[] params = {requestingPlayersList.getSelectionModel().getSelectedItem()};
            Future<String> result = pool.submit(new ServerNegotiationTask("acceptGameRequest", params));
            Alert alert;
            try {
                if (result.get().equals("success")) {  
                    confirmAndStartGame();
                }
                else if (result.get().equals("IOException")) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error connecting to Server. Please try again.");
                    alert.showAndWait();
                    startRefreshTimers();
                }
                else { // the game request no longer exists
                    requestingPlayersList.getItems().remove(params[1]); 
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Player Unavailable");
                    alert.setHeaderText(null);
                    alert.setContentText("The request you have accepted is no longer available. Please accept a different game request.");
                    alert.showAndWait();
                    startRefreshTimers();
                }      
            }
            catch (InterruptedException | ExecutionException e) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Execution Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error has occured in proccessing your request. Please try again.");
                    alert.showAndWait();  
                    startRefreshTimers();
            }
        });
        
        bottomButtonsHbox.getChildren().addAll(requestGameButton, acceptRequestButton);
        return bottomButtonsHbox;
   }
    
    private void confirmAndStartGame() {
        Timer timer = new Timer();
        AcceptGameConfirmer gameConfirmer = new AcceptGameConfirmer(this, timer);
        timer.schedule(gameConfirmer, 0, 5000);
    }
    
    public void startRefreshTimers() {
        playerListsRefresher = new PlayerListsRefresher(this);
        acceptedGameChecker = new AcceptedGameChecker(this);
        timer1 = new Timer();
        timer2 = new Timer();
        timer1.schedule(playerListsRefresher, 0, 5000);
        timer2.schedule(acceptedGameChecker, 0, 5000);
    }
    
    public void stopRefreshTimers() {
        acceptedGameChecker.cancel();
        playerListsRefresher.cancel();
        timer1.cancel();
        timer2.cancel();
    }
    
    public final void reset(String availability) {
        try {
            String[] params = {availability};
            Future<String> result = pool.submit(new ServerNegotiationTask("reset", params));
            if (!result.get().equals("success")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(null);
                alert.setContentText("Error connecting to Server. Available player and requests lists will not function properly.");
                alert.showAndWait();
            }
            
        } catch (InterruptedException | ExecutionException ex) {}
    }

    public Scene getHomeScene() {
        return homeScene;
    }

    public ListView<String> getAvailablePlayersList() {
        return availablePlayersList;
    }

    public ListView<String> getRequestingPlayersList() {
        return requestingPlayersList;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public User getUser() {
        return user;
    }
    public ExecutorService getPool() {
        return pool;
    }

}
