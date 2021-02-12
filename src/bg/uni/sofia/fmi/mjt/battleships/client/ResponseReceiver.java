package bg.uni.sofia.fmi.mjt.battleships.client;

import bg.uni.sofia.fmi.mjt.battleships.commands.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ResponseReceiver implements Runnable {

    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(2048);
    private static final String EXIT_MESSAGE = "Enter some text to close.";

    private final Set<String> gameExitResponses;
    private final Set<String> clientExitResponses;
    private final SocketChannel channel;
    private final Queue<String> configRequests;
    public ResponseReceiver(SocketChannel channel, Queue<String> configRequests) {
        gameExitResponses = new HashSet<>();
        clientExitResponses = new HashSet<>();
        this.channel = channel;
        this.configRequests = configRequests;
        clientExitResponses.add(Message.ALREADY_REGISTERED.toString());
        clientExitResponses.add(Message.ALREADY_LOGGED_IN.toString());
        clientExitResponses.add(Message.SUCCESSFUL_LOGOUT.toString());
    }
    @Override
    public void run() {
        while (true) {
            BUFFER.clear();
            try {
                channel.read(BUFFER);
            } catch (Exception e) {
                break;
            }
            BUFFER.flip();

            byte[] byteArray = new byte[BUFFER.remaining()];
            BUFFER.get(byteArray);
            String reply = new String(byteArray, StandardCharsets.UTF_8);
            System.out.print(reply);
            reply = reply.split(System.lineSeparator())[0];
            if(gameExitResponses.contains(reply)){
                configRequests.add("exit");
            }
            if (clientExitResponses.contains(reply)) {
                System.out.println(EXIT_MESSAGE);
                configRequests.add("logout");
                break;
            }
        }
    }
}
