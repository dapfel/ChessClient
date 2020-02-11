package ServerAccess;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author dapfel
 */

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer userID;
    private String username;
    private String password;
    private Integer wins;
    private Integer losses;
    private Integer draws;
    private Boolean available;
    private List<Gamerequest> gamerequestList;
    private List<Gamerequest> gamerequestList1;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(Integer userID) {
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    public List<Gamerequest> getGamerequestList() {
        return gamerequestList;
    }

    public void setGamerequestList(List<Gamerequest> gamerequestList) {
        this.gamerequestList = gamerequestList;
    }

    public List<Gamerequest> getGamerequestList1() {
        return gamerequestList1;
    }

    public void setGamerequestList1(List<Gamerequest> gamerequestList1) {
        this.gamerequestList1 = gamerequestList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userID != null ? userID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEntityClasses.Users[ userID=" + userID + " ]";
    }
    
}
