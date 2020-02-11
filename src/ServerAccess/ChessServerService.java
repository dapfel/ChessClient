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
     *         returns null if incorrect username or password
     * @throws IOException if there is a error in connecting to the server
     */
    public User validateSignIn(String username, String password) throws IOException {
        String url = BASE_URL + "user/signIn/" + username + "/" + password;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            User user = new Gson().fromJson(reader, User.class);
            return user;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * add a new user to the database (on first sign-up)
     * @return the user if successfully added.
     *         returns null if invalid input (such as if a user already exists for the given username)
     * @throws IOException if there is a error in connecting to the server
     */
    public User addUser(User user) throws IOException {
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
            return result;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
     /**
     * change a Users details. If not changing a value, set its parameter to null.
     * @return if successful - the user with the changes made.
     *         returns null if user doesn't exist or if invalid input
     * @throws IOException if error in connection to the server
     */
    public User updateUser(String username, User newUser) throws IOException {
        String url = BASE_URL + "user/update/" + username;
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        InputStreamReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Method", "PUT");
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(new Gson().toJson(newUser));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            User result = new Gson().fromJson(reader, User.class);
            reader.close();
            return result;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
     /**
     * get list of logged-in users that are available for game requests
     * @return list of usernames of available users.
     * @throws IOException if there is a error in connecting to the server
     */
    public UsernameList getAvailableUsers(String username) throws IOException {
        String url = BASE_URL + "user/availableUsers/";
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            UsernameList availableUsers = new Gson().fromJson(reader, UsernameList.class);
            availableUsers.remove(username);
            return availableUsers;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * get list of usernames of users that have made a game request to the client user
     * @return list of usernames of requesting users.
     * @throws IOException if there is a error in connecting to the server
     */
    public UsernameList getGameRequests(String username) throws IOException {
        String url = BASE_URL + "gameRequest/" + username;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            UsernameList requestingUsers = new Gson().fromJson(reader, UsernameList.class);
            return requestingUsers;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * Make a game request to another user
     * @return the request (if successfully made)
     *         returns null if either user doesn't exist or is unavailable
     * @throws IOException if there is a error in connecting to the server
     */
    public Gamerequest makeGameRequest(Gamerequest request) throws IOException {
        String url = BASE_URL + "gameRequest";
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
            writer.write(new Gson().toJson(request));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            request = new Gson().fromJson(reader, Gamerequest.class);
            reader.close();
            return request;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
     /**
     * accept a game request of another user
     * @return if successful - the game with the game ID
     *         returns null if user no longer available of if invalid input
     * @throws IOException if error in connection to the server
     */
    public Game acceptGameRequest(Gamerequest request) throws IOException {
        String url = BASE_URL + "gameRequest/accept";
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        InputStreamReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Method", "PUT");
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(new Gson().toJson(request));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            Game game = new Gson().fromJson(reader, Game.class);
            reader.close();
            return game;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }    
    
     /**
     * check if one of clients requests has been accepted to start game as white
     * @return if successful - the game with the game ID. if request not yet accepted, the returned game gameID will be 0
     *         returns null if invalid input
     * @throws IOException if there is a error in connecting to the server
     */
    public Gamerequest whiteStartGame(String username) throws IOException {
        String url = BASE_URL + "gameRequest/whiteStartGame/" + username;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            Gamerequest gameRequest = new Gson().fromJson(reader, Gamerequest.class);
            return gameRequest; //gameID of the request will be 0 if no requests accepted
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * start game as black after confirming that white has started
     * @return if successful - the game with the game ID
     *         returns null if user no longer available of if invalid input
     * @throws IOException if error in connection to the server
     */
    public Game blackStartGame(Gamerequest request) throws IOException {
        String url = BASE_URL + "gameRequest/blackStartGame";
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        InputStreamReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Method", "PUT");
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(new Gson().toJson(request));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            Game game = new Gson().fromJson(reader, Game.class);
            reader.close();
            return game;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
     /**
     * get the last move of opponent player
     * @return the last move.
     *         returns null if the game doesn't exist
     * @throws IOException if there is a error in connecting to the server
     */
    public String getLastMove(int gameID) throws  IOException {
        String url = BASE_URL + "game/lastMove/" + gameID;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            String move = new Gson().fromJson(reader, String.class);
            return move;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * make a chess move
     * @return if successful - the move made
     *         returns null if game doesn't exist
     * @throws IOException if error in connection to the server
     */
    public String makeMove(String move) throws IOException {
        String url = BASE_URL + "game/makeMove/" + move;
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        InputStreamReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Method", "PUT");
            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(new Gson().toJson(move));
            writer.flush();

            reader = new InputStreamReader(connection.getInputStream());
            move = new Gson().fromJson(reader, String.class);
            reader.close();
            return move;
        }
        catch(IOException e) {
            throw e;
        }
        finally {
            closeResources(connection,reader,writer);
        }
    }
    
     /**
     * end the game
     * @return the ended game
     *         returns null if the game doesn't exist
     * @throws IOException if there is a error in connecting to the server
     */
    public Game endGame(int gameID) throws IOException {
        String url = BASE_URL + "game/" + gameID;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            Game game = new Gson().fromJson(reader, Game.class);
            return game;
        }
        catch(IOException e) {
          throw e;
        }
    }
    
     /**
     * reset server for this user to no outstanding requests
     * @throws IOException if there is a error in connecting to the server
     */
    public String reset(String username) throws IOException {
        String url = BASE_URL + "gameRequest/reset/" + username;
        try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
            String response = new Gson().fromJson(reader, String.class);
            return response;
        }
        catch(IOException e) {
          throw e;
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
        catch(IOException e) {
            
        }
    }
}
