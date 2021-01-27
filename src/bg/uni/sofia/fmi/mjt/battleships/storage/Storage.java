package bg.uni.sofia.fmi.mjt.battleships.storage;

import java.nio.channels.SocketChannel;

public interface Storage {

    boolean isRegisteredUser(String username);

    boolean isLoggedInUser(String username);

    void registerUser(String username);

    void logInUser(String username, SocketChannel channel);
}
