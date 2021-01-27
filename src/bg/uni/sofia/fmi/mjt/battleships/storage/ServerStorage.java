package bg.uni.sofia.fmi.mjt.battleships.storage;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerStorage implements Storage {

    private final Map<String, SocketChannel> loggedInUsers;
    private final Map<SocketChannel,String> userOnChannel;
    private final Set<String> registeredUsers;

    public ServerStorage() {
        loggedInUsers = new HashMap<>();
        registeredUsers = new HashSet<>();
        userOnChannel = new HashMap<>();
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

}
