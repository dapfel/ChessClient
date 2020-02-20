package ChessGameLogic;

import ServerAccess.*;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 * @author dapfel
 */
public class ServerNegotiationTask implements Callable<String> {
    
    private final ChessServerService service;
    private final String task;
    private final String[] params;
    private static User user; // the client User
    private static String opponent; // opponent player's username
    private static UsernameList availableUsers; //users available for game request
    private static UsernameList requestingUsers; // users that have requested a game with the client user
    private static UsernameList requestedUsers; // users already requested by client 
    private static GameRequest gameRequest;
    private static Game game;
    
    public ServerNegotiationTask(String task, String[] params) {
        this.task = task;
        this.params = params;
        if (ServerNegotiationTask.requestedUsers == null)
            requestedUsers = new UsernameList();
        service = new ChessServerService();
    }
    
    @Override
    public String call(){  
        try {
            switch (task) {
                case "signIn":
                    user = service.validateSignIn(params[0], params[1]); 
                    if (user != null) 
                        return "success"; 
                    break;
                   
                case "updateUser":
                    // params[0] is new Username, params[1] is new password
                    user = service.updateUser(user.getUserID(), params[1], new User(params[0]));
                    if (user != null) 
                        return "success";
                    break;
                   
                case "addUser":
                    // params[0] is username, params[1] is password
                    user = service.addUser(new User(params[0]), params[1]); 
                    if (user != null) 
                        return "success";
                    break;
                    
                case "getAvailableUsers":    
                    availableUsers = service.getAvailableUsers();
                    availableUsers.removeAll(requestedUsers);
                    availableUsers.remove(user.getUsername());
                    if (availableUsers != null)
                        return "success";
                    break;
                    
                case "getRequestingUsers":
                    requestingUsers = service.getGameRequests(user.getUserID());
                    if (requestingUsers != null)
                        return "success";
                    break;
                
                case "requestGame":
                    // params[0] is requested players username
                    GameRequest request = new GameRequest(user.getUsername(), params[0]);
                    request = service.makeGameRequest(request);
                    if (request != null) {
                        requestedUsers.add(params[0]);
                        return "success";
                    }
                    break;
                    
                case "whiteStartGame":
                    gameRequest = service.whiteStartGame(user.getUserID());
                    if (gameRequest.getGameID() != 0) {
                        game = new Game();
                        game.setGameID(gameRequest.getGameID());
                        opponent = gameRequest.getRequestedUser();
                        return "success";
                    }
                    break;
                    
                case "acceptGameRequest":
                     //params[0] is requesting players username
                    gameRequest = new GameRequest(params[0], user.getUsername());
                    game = service.acceptGameRequest(gameRequest);
                    if (game != null) {
                        gameRequest.setGameID(game.getGameID());
                        return "success";   
                    }
                    break;
                    
                case "blackStartGame":
                    game = service.blackStartGame(gameRequest);
                    if (game != null) {
                        opponent = gameRequest.getRequestingUser();
                        return "success";
                    }
                    break;
                    
                case "endGame":
                    game = service.endGame(game.getGameID());
                    return "success";
                    
                case "reset":
                    // params[0] is availability
                    return reset(user.getUserID(), params[0]);
                    
            }
            return "failure";
        }
        catch (IOException e) {
            return "IOException";
        }
    }
    
    private String reset(int userID, String availability) throws IOException {
        if (!availability.equals("onlyAvailable")) {
            availableUsers = null; 
            requestingUsers = null;
            requestedUsers = null; 
            gameRequest = null;
            game = null;
            opponent = null;
        }
         return(service.reset(userID, availability));
    }

    public static User getUser() {
        return user;
    }

    public static Game getGame() {
        return game;
    }

    public static GameRequest getGameRequest() {
        return gameRequest;
    }

    public static String getOpponent() {
        return opponent;
    }

    public static void setOpponent(String opponent) {
        ServerNegotiationTask.opponent = opponent;
    }

    public static void setGame(Game game) {
        ServerNegotiationTask.game = game;
    }

    public static UsernameList getAvailableUsers() {
        return availableUsers;
    }

    public static UsernameList getRequestingUsers() {
        return requestingUsers;
    }
    
}
