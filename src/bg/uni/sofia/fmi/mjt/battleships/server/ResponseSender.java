package bg.uni.sofia.fmi.mjt.battleships.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;

public class ResponseSender implements Runnable {

    private static final int BUFFER_SIZE = 2048;
    private ByteBuffer buffer;
    private final Queue<Pair> pairs;
    public ResponseSender(Queue<Pair> pairs){
        this.pairs = pairs;
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (pairs) {
                if (pairs.size() == 0){
                    try {
                        pairs.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (Pair pair:
                     pairs) {
                    String response = pair.response();
                    SocketChannel clientChannel = pair.channel();
                    response = response + System.lineSeparator();
                    buffer.clear();
                    buffer.put(response.getBytes());
                    buffer.flip();
                    try {
                        clientChannel.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.compareTo("[ Disconnected from server ]" + System.lineSeparator()) == 0) {
                        try {
                            clientChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                pairs.clear();
            }
        }
    }
}
