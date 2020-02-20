package ChessGUI;

import ChessGameLogic.ServerNegotiationTask;
import ServerAccess.User;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
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
    private final ListeningExecutorService pool;
    private static PlayerListsRefresher playerListsRefresher;
    private static AcceptedGameChecker acceptedGameChecker;
    private static AcceptGameConfirmer gameConfirmer;
    private static Timer timer1;
    private static Timer timer2;
    private static Timer timer3;
    
    public HomePage(Stage primaryStage, ListeningExecutorService pool, User user) {
        
        this.user = user;
        this.primaryStage = primaryStage;
        this.pool = pool;
        
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
            stopRefreshTimers();
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

        ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("getAvailableUsers", new String[0]));
        Futures.addCallback(
            result,
            new FutureCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Platform.runLater( () -> {
                        if (response.equals("success")) {
                            ObservableList<String> items = FXCollections.observableArrayList(ServerNegotiationTask.getAvailableUsers());
                            availablePlayersList.setItems(items);
                        }
                    });
                }
                @Override
                public void onFailure(Throwable thrown) {}
                   
            },
            pool);     
        
        return availablePlayersList;
    }
    
    private ListView<String> RequestingPlayersList() {
        requestingPlayersList = new ListView<>();
        requestingPlayersList.setPrefWidth(70);
        requestingPlayersList.setPrefHeight(150);
        requestingPlayersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("getRequestingUsers", new String[0]));
        Futures.addCallback(
            result,
            new FutureCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Platform.runLater( () -> {
                        if (response.equals("success")) {
                            ObservableList<String> items = FXCollections.observableArrayList(ServerNegotiationTask.getRequestingUsers());
                            requestingPlayersList.setItems(items);
                        }
                    });
                }
                @Override
                public void onFailure(Throwable thrown) {}
                   
            },
            pool);                  
        
        return requestingPlayersList;
    }
    
    private HBox BottomButtonsHbox() {
        HBox bottomButtonsHbox = new HBox();
        bottomButtonsHbox.setPadding(new Insets(15, 12, 15, 12));
        bottomButtonsHbox.setSpacing(10);
        
        Button requestGameButton = new Button("Request Game");
        requestGameButton.setPrefSize(100, 20);
        requestGameButton.setOnMouseClicked(mouseEvent -> {
            String requestedPlayer = availablePlayersList.getSelectionModel().getSelectedItem();
            ProgressDialog progressDialog = new ProgressDialog("Requesting game with " + requestedPlayer);
            progressDialog.show();
                   
            String[] params = {requestedPlayer};
            ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("requestGame", params));
            Futures.addCallback(
                result,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            completeGameRequest(response, requestedPlayer, primaryStage, pool);
                        });
                    }
                    @Override
                    public void onFailure(Throwable thrown) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Execution Error");
                            alert.setHeaderText(null);
                            alert.setContentText("An error has occured in proccessing your request. Please try again.");
                            alert.showAndWait();   
                        });
                    }
                },
                pool);
        });

        Button acceptRequestButton = new Button("Accept Request");
        acceptRequestButton.setPrefSize(100, 20);
        acceptRequestButton.setOnMouseClicked(mouseEvent -> {
            String requestingPlayer = requestingPlayersList.getSelectionModel().getSelectedItem();
            ProgressDialog progressDialog = new ProgressDialog("Accepting game with " + requestingPlayer);
            progressDialog.show();
            
            stopRefreshTimers();
            String[] params = {requestingPlayer};
            ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("acceptGameRequest", params));
            Futures.addCallback(
                result,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Platform.runLater( () -> {
                            completeAcceptingGame(response, requestingPlayer, progressDialog);
                        });
                    }
                    @Override
                    public void onFailure(Throwable thrown) {
                        Platform.runLater( () -> {
                            progressDialog.close();
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Execution Error");
                            alert.setHeaderText(null);
                            alert.setContentText("An error has occured in proccessing your request. Please try again.");
                            alert.showAndWait();  
                            startRefreshTimers();
                        });
                    }
                },
                pool);      
        });
        
        bottomButtonsHbox.getChildren().addAll(requestGameButton, acceptRequestButton);
        return bottomButtonsHbox;
   }
    
    public void startRefreshTimers() {
        reset("available"); // reset to no games or requests for the client user and that available
        playerListsRefresher = new PlayerListsRefresher(this);
        acceptedGameChecker = new AcceptedGameChecker(this);
        timer1 = new Timer();
        timer2 = new Timer();
        timer1.schedule(playerListsRefresher, 5000, 5000);
        timer2.schedule(acceptedGameChecker, 5000, 5000);
    }
    
    public static void stopRefreshTimers() {
        if (timer1 != null && timer2 != null) {
            timer1.cancel();
            timer2.cancel();
        }
        if (timer3 != null) {
            timer3.cancel();
        }
    }
    
    public final void reset(String availability) {
        String[] params = {availability};
        ProgressDialog progressDialog = new ProgressDialog("Initializing Home Page");
        progressDialog.show();
        
        ListenableFuture<String> result = pool.submit(new ServerNegotiationTask("reset", params));
        Futures.addCallback(
            result,
            new FutureCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Platform.runLater( () -> {
                        progressDialog.close();
                        if (!"success".equals(response)) { //IOException
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Connection Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Error connecting to Server. Available player and requests lists will not function properly.");
                            alert.showAndWait();
                        }
                    });
                }
                @Override
                public void onFailure(Throwable thrown) {
                    Platform.runLater( () -> {
                        progressDialog.close();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Execution Error");
                        alert.setHeaderText(null);
                        alert.setContentText("An error has occured in initializng the page. Available player and requests lists will not function properly.");
                        alert.showAndWait();  
                        startRefreshTimers();
                    });
                }
            },
            pool);
    }
    
    private void completeGameRequest(String response, String requestedPlayer, Stage primaryStage, ListeningExecutorService pool) {
        if ("success".equals(response)) {
            availablePlayersList.getItems().remove(requestedPlayer);                 
        }
        else {//IOException
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Error connecting to Server. Please try again.");
            alert.showAndWait();
        }
    }
    
    private void completeAcceptingGame(String response, String requestingPlayer, ProgressDialog progressDialog) {
        Alert alert;
        if (response.equals("success")) { 
            confirmAndStartGame(progressDialog);
        }
        else if (response.equals("IOException")) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("Error connecting to Server. Please try again.");
            alert.showAndWait();
            startRefreshTimers();
        }
        else { // the game request no longer exists
            requestingPlayersList.getItems().remove(requestingPlayer); 
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Player Unavailable");
            alert.setHeaderText(null);
            alert.setContentText("The request you have accepted is no longer available. Please accept a different game request.");
            alert.showAndWait();
            startRefreshTimers();
        }        
    }
    
    private void confirmAndStartGame(ProgressDialog progressDialog) {
        timer3 = new Timer();
        gameConfirmer = new AcceptGameConfirmer(this, timer3, progressDialog);
        timer3.schedule(gameConfirmer, 1000, 5000);
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
    public ListeningExecutorService getPool() {
        return pool;
    }

}
