package bg.uni.sofia.fmi.mjt.battleships.server;

import bg.uni.sofia.fmi.mjt.battleships.commands.Command;
import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.storage.ServerStorage;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;
import bg.uni.sofia.fmi.mjt.battleships.commands.CommandExecutor;
import bg.uni.sofia.fmi.mjt.battleships.commands.Executor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class GameServerThread extends Thread {
    private static final String GAMES_FILENAME = "saved-games";
    private static final String SERVER_HOST = "localhost";
    private final int serverPort;
    private static final int BUFFER_SIZE = 2048;
    private boolean isRunning;
    private final Executor executor;
    private final Storage storage;
    private Selector selector;
    private ByteBuffer buffer;
    private final Queue<Pair> responsesQueue;
    private ResponseSender sender;

    public GameServerThread(int port) {
        serverPort = port;
        storage = new ServerStorage();
        responsesQueue = new LinkedList<>();
        executor = new CommandExecutor(storage, new FileHandler(Path.of(GAMES_FILENAME), storage), responsesQueue);
        sender = new ResponseSender(responsesQueue);
        new Thread(sender).start();
    }

    public void stopServer() {
        isRunning = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private String getRequest(SocketChannel clientChannel) {
        buffer.clear();
        int r;
        try {
            r = clientChannel.read(buffer);
        } catch (IOException e) {
            return null;
        }
        if (r <= 0) {
            System.out.println("nothing to read, will close channel");
            try {
                storage.logOutUser(storage.getUserOnChannel(clientChannel));
                clientChannel.close();
            } catch (IOException e) {
                return null;
            }
            return null;
        }
        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private void sendToClient(String response, SocketChannel clientChannel) throws IOException {
        response = response + System.lineSeparator();
        buffer.clear();
        buffer.put(response.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
        if (response.compareTo("[ Disconnected from server ]" + System.lineSeparator()) == 0) {
            clientChannel.close();
        }
    }

    @Override
    public void run() {
        isRunning = true;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (isRunning) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel currentChannel = (SocketChannel) key.channel();
                        String request = getRequest(currentChannel);
                        if (request == null) {
                            break;
                        }
                        request = request.replace(System.lineSeparator(), "");
                        executor.executeCommand(new Command(request), currentChannel);
                        //String response = "Default response";
                        //sendToClient(response, currentChannel);
                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.err.print("IO exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
