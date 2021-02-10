package bg.uni.sofia.fmi.mjt.battleships.server;

import java.nio.channels.SocketChannel;

public record Pair(String response, SocketChannel channel) {
}
