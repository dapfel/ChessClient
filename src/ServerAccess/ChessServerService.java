package ServerAccess;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author dapfel
 */
public class ChessServerService {
    
    private static final String BASE_URL = "http://localhost:8080/ChessServer/webresources/";
    
     /**
     * validate if correct password (on sign-in)
     * @return if correct - the user.
     * @throws Exception if incorrect username or password - with exception message "invalid username or password"
     * @throws IOException if there is a error in connecting to the server
     */
    public User validateSignIn(String username, String password) throws Exception, IOException {
        String url = BASE_URL + "user/signIn/" + username + "/" + password;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            User user = new Gson().fromJson(reader, User.class);
            if (user == null)
                throw new Exception("invalid email or password");
            return user;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * add a new user to the database (on first sign-up)
     * @return the user if successfully added.
     * @throws Exception if invalid input (such as if a user already exists for the given username) with exception message "invalid input"
     * @throws IOException if there is a error in connecting to the server
     */
    public User addUser(User user) throws Exception, IOException {
        String url = BASE_URL + "user";
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        InputStreamReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Method", "POST");
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(new Gson().toJson(user));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            User result = new Gson().fromJson(reader, User.class);
            reader.close();
            if (result == null)
                throw new Exception("invalid input");
            else
                return result;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
    
    
    private void closeResources(HttpURLConnection connection,InputStreamReader reader, OutputStreamWriter writer) {
        try {
            if (connection != null)
                connection.disconnect();
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
        }
        catch(Exception e) {
            
        }
    }
}
