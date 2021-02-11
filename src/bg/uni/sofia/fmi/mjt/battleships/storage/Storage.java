package bg.uni.sofia.fmi.mjt.battleships.storage;

import bg.uni.sofia.fmi.mjt.battleships.commands.UserStatus;
import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Ship;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;

import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public interface Storage {

    boolean isRegisteredUser(String username);

    boolean isLoggedInUser(String username);

    void registerUser(String username);

    void logInUser(String username, SocketChannel channel);

    String getUserOnChannel(SocketChannel channel);

    void logOutUser(String username);

    Set<String> getGameNames();

    Collection<Board> getGames();

    boolean containsGameName(String name);

    boolean containsGame(String gameName);

    boolean isUserInGame(String username);

    void addGame(String name, Board board);

    void joinAGame(String username, String gameName, Table table);

    String getCurrentGame(String username);

    Board getGameByName(String name);

    void leaveGameWithoutSaving(String username);

    String getGameOutput(String username);

    String attack(String username, Point point);

    UserStatus getUserStatus(String username);

    void setUserStatus(String username, UserStatus status);

    void addSavedGame(String username,String gameName, Path filePath);

    void removeGame(String gameName);

    Path getSavedGame(String username, String gameName);

    Collection<String> getSavedGames(String username);

    void continuePlaying(String username, String gameName);

    SocketChannel getChannel(String username);

    String getOpponent(String username);
}
