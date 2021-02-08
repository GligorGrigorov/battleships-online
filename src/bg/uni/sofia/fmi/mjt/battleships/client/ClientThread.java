package bg.uni.sofia.fmi.mjt.battleships.client;

import bg.uni.sofia.fmi.mjt.battleships.commands.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientThread extends Thread {
    private final int serverPort;
    private final String serverHost;
    private final String username;
    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(2048);

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
            List<String> configInputs = new ArrayList<>();
            System.out.println("Connected");
            configInputs.add("login");
            int i = 0;
            while (true) {
                String message;
                if (i < configInputs.size()) {
                    message = configInputs.get(i);
                } else {
                    message = scanner.nextLine();
                }
                i++;
                message = message + " " + username + System.lineSeparator();
                BUFFER.clear();
                BUFFER.put(message.getBytes());
                BUFFER.flip();
                socketChannel.write(BUFFER);

                BUFFER.clear();
                socketChannel.read(BUFFER);
                BUFFER.flip();

                byte[] byteArray = new byte[BUFFER.remaining()];
                BUFFER.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8);
                System.out.print(reply);
                reply = reply.split(System.lineSeparator())[0];
                if (reply.equals(Message.ALREADY_REGISTERED.toString())) {
                    break;
                }
                if (reply.equals(Message.ALREADY_LOGGED_IN.toString())) {
                    break;
                }
                if (reply.equals(Message.SUCCESSFUL_LOGOUT.toString())) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.print("IO exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
