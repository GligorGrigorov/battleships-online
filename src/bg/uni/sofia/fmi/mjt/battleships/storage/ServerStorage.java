package bg.uni.sofia.fmi.mjt.battleships.storage;

import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;

import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerStorage implements Storage {

    private final Map<String, SocketChannel> loggedInUsers;
    private final Map<SocketChannel,String> userOnChannel;
    private final Set<String> registeredUsers;
    private final Map<String, Board> games;
    private final Map<String, Board> inGameUsers;

    public ServerStorage() {
        loggedInUsers = new HashMap<>();
        registeredUsers = new HashSet<>();
        userOnChannel = new HashMap<>();
        games = new HashMap<>();
        inGameUsers = new HashMap<>();
    }

    @Override
    public boolean isRegisteredUser(String username) {
        return registeredUsers.contains(username);
    }

    @Override
    public boolean isLoggedInUser(String username) {
        return loggedInUsers.containsKey(username);
    }

    @Override
    public void registerUser(String username) {
        registeredUsers.add(username);
    }

    @Override
    public void logInUser(String username, SocketChannel channel) {
        loggedInUsers.put(username, channel);
        userOnChannel.put(channel,username);
    }

    @Override
    public String getUserOnChannel(SocketChannel channel) {
        return userOnChannel.get(channel);
    }

    @Override
    public void logOutUser(String username) {
        userOnChannel.remove(loggedInUsers.get(username));
        loggedInUsers.remove(username);
    }

    @Override
    public Set<String> getGameNames() {
        return games.keySet();
    }

    @Override
    public Collection<Board> getGames() {
        return games.values();
    }

    @Override
    public boolean containsGameName(String name) {
        return games.containsKey(name);
    }

    @Override
    public boolean containsGame(Board game) {
        return games.containsValue(game);
    }

    @Override
    public boolean isUserInGame(String username) {
        return inGameUsers.containsKey(username);
    }

    @Override
    public void addGame(String name, Board board) {
        games.put(name,board);
    }

    @Override
    public void joinAGame(String username, Board board) {
        inGameUsers.put(username,board);
    }
}
