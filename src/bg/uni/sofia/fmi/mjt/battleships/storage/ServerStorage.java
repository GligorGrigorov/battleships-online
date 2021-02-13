package bg.uni.sofia.fmi.mjt.battleships.storage;

import bg.uni.sofia.fmi.mjt.battleships.commands.UserStatus;
import bg.uni.sofia.fmi.mjt.battleships.exceptions.GameNotAvailableException;
import bg.uni.sofia.fmi.mjt.battleships.exceptions.PlayerNotAvailableException;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;

import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.*;

public class ServerStorage implements Storage {

    private final Map<String, SocketChannel> loggedInUsers;
    private final Map<SocketChannel, String> userOnChannel;
    private final Set<String> registeredUsers;
    private final Map<String, Game> games;
    private final Map<String, String> inGameUsers;
    private final Map<String, UserStatus> userStatusMap;
    private final Map<String, Map<String, Path>> savedGames;

    public ServerStorage() {
        loggedInUsers = new HashMap<>();
        registeredUsers = new HashSet<>();
        userOnChannel = new HashMap<>();
        games = new HashMap<>();
        inGameUsers = new HashMap<>();
        userStatusMap = new HashMap<>();
        savedGames = new HashMap<>();
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
        userOnChannel.put(channel, username);
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
    public Collection<Game> getGames() {
        return games.values();
    }

    @Override
    public boolean containsGameName(String name) {
        return games.containsKey(name);
    }

    @Override
    public boolean isUserInGame(String username) {
        return inGameUsers.containsKey(username);
    }

    @Override
    public void addGame(String name, Game game) {
        games.put(name, game);
    }

    @Override
    public void joinAGame(String username, String gameName, Table table) {
        inGameUsers.put(username, gameName);
        games.get(gameName).addPlayer(username, table);
    }

    @Override
    public String getCurrentGame(String username) {
        if (!inGameUsers.containsKey(username)) {
            return null;
        }
        return inGameUsers.get(username);
    }

    @Override
    public Game getGameByName(String name) throws GameNotAvailableException {
        if (!games.containsKey(name)) {
            throw new GameNotAvailableException("Game not available");
        }
        return games.get(name);
    }

    @Override
    public String leaveGameWithoutSaving(String username) {
        String gameName = inGameUsers.get(username);
        Game game = games.get(inGameUsers.get(username));
        game.surrender(username);
        String output = game.getOutput(username);
        setUserStatus(username, UserStatus.IN_MAIN_MENU);
        inGameUsers.remove(username);
        if (game.getNumberOfPlayers() == 0) {
            removeGame(gameName);
        }
        return output;
    }

    @Override
    public String getGameOutput(String username) {
        String user = inGameUsers.get(username);
        if (user == null) {
            return "USEr is null";
        }
        Game game = games.get(user);
        if (game == null) {
            return "GAme is null";
        }
        return games.get(inGameUsers.get(username)).getOutput(username);
    }

    @Override
    public String attack(String username, Point point) {
        return games.get(inGameUsers.get(username)).attack(username, point);
    }

    @Override
    public UserStatus getUserStatus(String username) {
        return userStatusMap.get(username);
    }

    @Override
    public void setUserStatus(String username, UserStatus status) {
        userStatusMap.put(username, status);
    }

    @Override
    public void addSavedGame(String username, String gameName, Path filePath) {
        savedGames.computeIfAbsent(username, k -> new HashMap<>());
        savedGames.get(username).put(gameName, filePath);
    }

    @Override
    public void removeGame(String gameName) {
        games.remove(gameName);
    }

    @Override
    public Path getSavedGame(String username, String gameName) {
        return savedGames.get(username).get(gameName);
    }

    @Override
    public Collection<String> getSavedGames(String username) {
        if (savedGames.get(username) == null) {
            return new HashSet<>();
        }
        return savedGames.get(username).keySet();
    }

    @Override
    public void continuePlaying(String username, String gameName) {
        inGameUsers.put(username, gameName);
    }

    @Override
    public SocketChannel getChannel(String username) {
        return loggedInUsers.get(username);
    }

    @Override
    public String getOpponent(String username) throws PlayerNotAvailableException {
        Game game = getGameByName(getCurrentGame(username));
        if (game == null) {
            return null;
        }
        try {
            return game.getOpponent().equals(username) ? game.getCreator() : game.getOpponent();
        } catch (RuntimeException e) {
            throw new PlayerNotAvailableException("Opponent not available.");
        }
    }
}
