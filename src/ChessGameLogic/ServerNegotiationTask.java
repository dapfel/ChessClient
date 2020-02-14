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
    private static UsernameList requestedUsers; // users already requested by client users
    private static Gamerequest gameRequest;
    private static Game game;
    
    public ServerNegotiationTask(String task, String[] params) {
        this.task = task;
        this.params = params;
        service = new ChessServerService();
    }
    
    @Override
    public String call(){  
        try {
            switch (task) {
                case "signIn":
                    // params[0] is username, params[1] is password
                    user = service.validateSignIn(params[0], params[1]); 
                    if (user != null) 
                        return "success";
                    
                case "updateUser":
                    // params[0] is new Username, params[1] is new password
                    user = service.updateUser(user.getUserID(), new User(params[0],params[1]));
                    if (user != null) 
                        return "success";
                        
                case "addUser":
                    // params[0] is username, params[1] is password
                    user = service.addUser(new User(params[0], params[1])); 
                    if (user != null) 
                        return "success";
                    
                case "getAvailableUsers":    
                    availableUsers = service.getAvailableUsers(user.getUsername());
                    availableUsers.removeAll(requestedUsers);
                    if (availableUsers != null)
                        return "success";
                    
                case "getRequestingUsers":
                    requestingUsers = service.getGameRequests(user.getUserID());
                    if (requestingUsers != null)
                        return "success";
                
                case "requestGame":
                    // params[0] is requested players username
                    Gamerequest request = new Gamerequest(user.getUsername(), params[0]);
                    request.setUser1(user);
                    request = service.makeGameRequest(request);
                    if (request != null) {
                        requestedUsers.add(params[0]);
                        return "success";
                    }
                case "whiteStartGame":
                    gameRequest = service.whiteStartGame(user.getUsername());
                    if (gameRequest.getGameID() != 0) {
                        game.setGameID(gameRequest.getGameID());
                        opponent = gameRequest.getGamerequestPK().getRequestedUser();
                        return "success";
                    }
                    
                case "acceptGameRequest":
                     //params[0] is requesting players username
                    gameRequest = new Gamerequest(params[0], user.getUsername());
                    game = service.acceptGameRequest(gameRequest);
                    if (game != null) {
                        gameRequest.setGameID(game.getGameID());
                        return "success";   
                    }
                    
                case "blackStartGame":
                    game = service.blackStartGame(gameRequest);
                    if (game != null) {
                        opponent = gameRequest.getGamerequestPK().getRequestingUser();
                        return "success";
                    }
                    
                case "endGame":
                    game = service.endGame(game.getGameID());
                    return "success";
                    
                case "reset":
                    return reset(user.getUserID(), params[0]);
                    
                default : return null;
            }
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

    public static Gamerequest getGameRequest() {
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
