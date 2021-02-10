package bg.uni.sofia.fmi.mjt.battleships.commands;

import java.nio.channels.SocketChannel;

public interface Executor {
    void executeCommand(Command command, SocketChannel channel);
}
