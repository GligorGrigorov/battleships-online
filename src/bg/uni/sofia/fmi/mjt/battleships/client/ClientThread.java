package bg.uni.sofia.fmi.mjt.battleships.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

public class ClientThread extends Thread {

    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(2048);

    private final int serverPort;
    private final String serverHost;
    private final String username;

    public ClientThread(String host, int port, String username) {
        serverHost = host;
        serverPort = port;
        this.username = username;
    }

    @Override
    public void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            Queue<String> configInputs = new LinkedList<>();
            Thread receiver = new Thread(new ResponseReceiver(socketChannel, configInputs));
            configInputs.add("login");
            receiver.start();
            do {
                String message;
                if (configInputs.size() > 0) {
                    message = configInputs.poll();
                } else {
                    if (scanner.hasNext()) {
                        message = scanner.nextLine();
                    } else {
                        break;
                    }
                }
                message = message + " " + username + System.lineSeparator();
                BUFFER.clear();
                BUFFER.put(message.getBytes());
                BUFFER.flip();
                socketChannel.write(BUFFER);
            } while (receiver.isAlive());
            System.out.println("Closing game client...");
        } catch (IOException e) {
            System.err.print("IO exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
