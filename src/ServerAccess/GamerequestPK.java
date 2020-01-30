package ServerAccess;

import java.io.Serializable;

/**
 *
 * @author dapfel
 */
public class GamerequestPK implements Serializable {

    private String requestingUser;
    private String requestedUser;

    public GamerequestPK() {
    }

    public GamerequestPK(String requestingUser, String requestedUser) {
        this.requestingUser = requestingUser;
        this.requestedUser = requestedUser;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requestingUser != null ? requestingUser.hashCode() : 0);
        hash += (requestedUser != null ? requestedUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GamerequestPK)) {
            return false;
        }
        GamerequestPK other = (GamerequestPK) object;
        if ((this.requestingUser == null && other.requestingUser != null) || (this.requestingUser != null && !this.requestingUser.equals(other.requestingUser))) {
            return false;
        }
        if ((this.requestedUser == null && other.requestedUser != null) || (this.requestedUser != null && !this.requestedUser.equals(other.requestedUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEntityClasses.GamerequestPK[ requestingUser=" + requestingUser + ", requestedUser=" + requestedUser + " ]";
    }
    
}
