package bg.uni.sofia.fmi.mjt.battleships.storage;

import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    boolean containsGame(Board game);

    boolean isUserInGame(String username);

    void addGame(String name, Board board);

    void joinAGame(String username,Board board);

}
