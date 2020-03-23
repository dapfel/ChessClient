package ServerAccess;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 * @author dapfel
 */
public class ServerNegotiationTask implements Callable<String> {
    
    public enum Task {SIGN_IN, UPDATE_USER, ADD_USER, GET_AVAILABLE_USERS, GET_REQUESTING_USERS, 
                     REQUEST_GAME, WHITE_START_GAME, ACCEPT_GAME_REQUEST, BLACK_START_GAME, MAKE_MOVE,
                     GET_LAST_MOVE, END_GAME, RESET}
    
    private final ChessServerService service;
    private final Task task;
    private final String[] params;
    private static User user; // the client User
    private static String opponent; // opponent player's username
    private static UsernameList availableUsers; //users available for game request
    private static UsernameList requestingUsers; // users that have requested a game with the client user
    private static UsernameList requestedUsers; // users already requested by client 
    private static GameRequest gameRequest;
    private static Game game;
    private static boolean firstMove;
    
    public ServerNegotiationTask(Task task, String[] params) {
        this.task = task;
        this.params = params;
        if (ServerNegotiationTask.availableUsers == null)
            availableUsers = new UsernameList();
        if (ServerNegotiationTask.requestingUsers == null)
            requestingUsers = new UsernameList();
        if (ServerNegotiationTask.requestedUsers == null)
            requestedUsers = new UsernameList();
        service = new ChessServerService();
    }
    
    @Override
    public String call(){  
        try {
            switch (task) {
                case SIGN_IN:
                    user = service.validateSignIn(params[0], params[1]); 
                    if (user != null) 
                        return "success"; 
                    break;
                   
                case UPDATE_USER:
                    // params[0] is new Username, params[1] is new password
                    User tempUser = service.updateUser(user.getUserID(), params[1], new User(params[0]));
                    if (tempUser != null)
                            user = tempUser;
                    return "success";
                   
                case ADD_USER:
                    // params[0] is username, params[1] is password
                    user = service.addUser(new User(params[0]), params[1]); 
                    if (user != null) 
                        return "success";
                    break;
                    
                case GET_AVAILABLE_USERS:    
                    availableUsers = service.getAvailableUsers();
                    availableUsers.removeAll(requestedUsers);
                    availableUsers.remove(user.getUsername());
                    if (availableUsers != null)
                        return "success";
                    break;
                    
                case GET_REQUESTING_USERS:
                    requestingUsers = service.getGameRequests(user.getUserID());
                    if (requestingUsers != null)
                        return "success";
                    break;
                
                case REQUEST_GAME:
                    // params[0] is requested players username
                    GameRequest request = new GameRequest(user.getUsername(), params[0]);
                    request = service.makeGameRequest(request);
                    if (request != null) {
                        requestedUsers.add(params[0]);
                        return "success";
                    }
                    break;
                    
                case WHITE_START_GAME:
                    gameRequest = service.whiteStartGame(user.getUserID());
                    if (gameRequest.getGameID() != 0) {
                        game = new Game();
                        game.setGameID(gameRequest.getGameID());
                        opponent = gameRequest.getRequestedUser();
                        firstMove = true;
                        return "success";
                    }
                    break;
                    
                case ACCEPT_GAME_REQUEST:
                     // params[0] is requesting players username
                    gameRequest = new GameRequest(params[0], user.getUsername());
                    game = service.acceptGameRequest(gameRequest);
                    if (game != null) {
                        gameRequest.setGameID(game.getGameID());
                        return "success";   
                    }
                    break;
                    
                case BLACK_START_GAME:
                    game = service.blackStartGame(gameRequest);
                    if (game != null) {
                        opponent = gameRequest.getRequestingUser();
                        firstMove = true;
                        return "success";
                    }
                    break;
                    
                case MAKE_MOVE:
                    // params[0] is the move
                    String lastMove = service.makeMove(game.getGameID(), params[0]);
                    if (lastMove != null) {
                        firstMove = false;
                        game.setMove(lastMove);
                        return "success";
                    }
                    break;
                      
                case GET_LAST_MOVE:
                    lastMove = service.getLastMove(game.getGameID());
                    if (lastMove != null || firstMove == true) {
                        game.setMove(lastMove);
                        return "success";
                    }
                    break;
                    
                case END_GAME:
                    game = service.endGame(game.getGameID());
                    return "success";
                    
                case RESET:
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
            availableUsers.clear(); 
            requestingUsers.clear();
            requestedUsers.clear(); 
            gameRequest = null;
            game = null;
            opponent = null;
            firstMove = true;
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

    public static void setFirstMove(boolean firstMove) {
        ServerNegotiationTask.firstMove = firstMove;
    }
    
}
