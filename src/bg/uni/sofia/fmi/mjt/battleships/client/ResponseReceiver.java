package bg.uni.sofia.fmi.mjt.battleships.client;

import bg.uni.sofia.fmi.mjt.battleships.commands.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

public record ResponseReceiver(SocketChannel channel, Queue<String> configInputs) implements Runnable {

    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(2048);
    private static final String EXIT_MESSAGE = "Enter some text to close.";
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
            if(reply.equals("WIN")){
                System.out.println(EXIT_MESSAGE);
                configInputs.add("exit");
            }
            if (reply.equals(Message.ALREADY_REGISTERED.toString())) {
                System.out.println(EXIT_MESSAGE);
                configInputs.add("logout");
                break;
            }
            if (reply.equals(Message.ALREADY_LOGGED_IN.toString())) {
                System.out.println(EXIT_MESSAGE);
                configInputs.add("logout");
                break;
            }
            if (reply.equals(Message.SUCCESSFUL_LOGOUT.toString())) {
                System.out.println(EXIT_MESSAGE);
                configInputs.add("logout");
                break;
            }
        }
    }
}
