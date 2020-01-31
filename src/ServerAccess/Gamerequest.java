package ServerAccess;

import ServerAccess.GamerequestPK;
import ServerAccess.User;
import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public class Gamerequest implements Serializable {

    private static final long serialVersionUID = 1L;
    protected GamerequestPK gamerequestPK;
    private Integer gameID;
    private User user;
    private User user1;

    public Gamerequest() {
    }

    public Gamerequest(GamerequestPK gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public Gamerequest(String requestingUser, String requestedUser) {
        this.gamerequestPK = new GamerequestPK(requestingUser, requestedUser);
    }

    public GamerequestPK getGamerequestPK() {
        return gamerequestPK;
    }

    public void setGamerequestPK(GamerequestPK gamerequestPK) {
        this.gamerequestPK = gamerequestPK;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gamerequestPK != null ? gamerequestPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gamerequest)) {
            return false;
        }
        Gamerequest other = (Gamerequest) object;
        if ((this.gamerequestPK == null && other.gamerequestPK != null) || (this.gamerequestPK != null && !this.gamerequestPK.equals(other.gamerequestPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEntityClasses.Gamerequest[ gamerequestPK=" + gamerequestPK + " ]";
    }
    
}
