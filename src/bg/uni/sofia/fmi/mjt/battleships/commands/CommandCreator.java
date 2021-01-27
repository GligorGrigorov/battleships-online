package bg.uni.sofia.fmi.mjt.battleships.commands;

public class CommandCreator {
    public static Command newCommand(String input){
        String[] tokens = input.replaceAll("\\s{2,}", " ").trim().split(" ");
        if(tokens.length == 0 || tokens.length > 3){
            return null;
        }
        Command command;
        if(tokens.length == 2){
            command = new Command(tokens[1], tokens[0],null);
        } else {
            command = new Command(tokens[2],tokens[0],tokens[1]);
        }
        if (Commands.containsCommand(command)){
            return command;
        }
        return null;
    }
}
