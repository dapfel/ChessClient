package ServerAccess;

import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public class GameRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer gameID;
    private String requestingUser;
    private String requestedUser;

    public GameRequest() {
    }

    public GameRequest(String requestingUser, String requestedUser) {
        this.requestingUser = requestingUser;
        this.requestedUser = requestedUser;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
    }

    public String getRequestedUser() {
        return requestedUser;
    }

    public void setRequestedUser(String requestedUser) {
        this.requestedUser = requestedUser;
    }


    
}
