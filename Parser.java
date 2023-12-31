package com.mycompany.terminal;

import java.util.Arrays;

public class Parser {
    private String command_line ;
    private String[] args ;

    public boolean parse(String input) {
        String[] tokens = input.trim().split(" "); // Split the input string into an array of tokens using space as the delimiter
        if (tokens.length > 0) {
            command_line = tokens[0]; // The first token is the command name
            args = Arrays.copyOfRange(tokens, 1, tokens.length); // The remaining tokens are the arguments
            return true;
        }
        return false;
    }

    public String getCommandName() {   //get command_name
        return command_line;
    }

    public String[] getArgs() {       //get arguments
        return args;
    }

}
