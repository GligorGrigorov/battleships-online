package bg.uni.sofia.fmi.mjt.battleships.commands;

public class Command {
    private final String name;
    private final String argument;
    private final String username;
    public Command(String username, String name, String argument){
        this.name = name;
        this.argument = argument;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getArgument() {
        return argument;
    }

    public String getName() {
        return name;
    }
     public Boolean containsArgument(){
        return argument != null;
     }
}
