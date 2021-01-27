package bg.uni.sofia.fmi.mjt.battleships.main;

import bg.uni.sofia.fmi.mjt.battleships.client.ClientThread;

public class ClientMain {
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Wrong number of arguments");
            return;
        }
        Thread client = new ClientThread("localhost",7777, args[0]);
        client.start();
    }
}
