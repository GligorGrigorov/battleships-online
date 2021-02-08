package bg.uni.sofia.fmi.mjt.battleships.main;

import bg.uni.sofia.fmi.mjt.battleships.server.GameServerThread;

import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        GameServerThread server = new GameServerThread(7777);
        server.start();
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.println("Command:");
            input = scanner.nextLine();
        } while (!input.equals("stop"));
        server.stopServer();
    }

}
