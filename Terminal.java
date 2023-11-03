package com.mycompany.terminal;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Terminal {
    private Parser parser;
    private List<String> command_History = new ArrayList<>();

    public Terminal() {
        parser = new Parser();
    }

    //Print working directory
    public void pwd() {
        String current_Directory = System.getProperty("user.dir");
        System.out.println("Current directory: " + current_Directory);
    }

    //Print any text that follows the command
    public void echo(String[] args) {
        if (args.length == 1) {
            System.out.println(args[0]);
        } else {
            System.out.println("Usage: echo <message>");
        }
    }

    // list directories and files
    public void ls() {
        File current_Directory = new File(System.getProperty("user.dir"));
        File[] contents = current_Directory.listFiles();
        if (contents != null) {
            Arrays.sort(contents); // Sort files and directories alphabetically
            for (File file : contents) {
                System.out.println(file.getName());
            }
        }
    }

    // navigate through directories
    public void cd(String[] args) {
        if (args.length == 0) {                 // Case 1: cd with no arguments changes the current directory to the home directory
            String userHome = System.getProperty("user.home");
            System.setProperty("user.dir", userHome);
        } else if (args.length == 1) {
            if (args[0].equals("..")) { // Case 2: cd with ".." argument changes the current directory to the previous directory
                String currentDir = System.getProperty("user.dir");
                File current_File = new File(currentDir);
                String parent_Directory = current_File.getParent();
                if (parent_Directory != null) {
                    System.setProperty("user.dir", parent_Directory);
                } else {
                    System.out.println("Already at the root directory.");
                }
            } else {                          // Case 3: cd with a path argument changes the current directory to the specified path
                String new_Path = args[0];
                File new_Directory = new File(new_Path);

                if (new_Directory.isDirectory()) {
                    System.setProperty("user.dir", new_Directory.getAbsolutePath());
                } else {
                    System.out.println("Directory not found: " + new_Path);
                }
            }
        } else {
            System.out.println("Usage: cd [directory]");
        }
    }

    //takes a name of a file and removes it from the current directory
    public void rm(String[] args) {
        String FileName = args[0];
        File file = new File(FileName);

        if (file.exists()) {
            file.delete();
            System.out.println("File deleted!");
        } else {
            System.out.println("File " + FileName + " does not exist.");
        }
    }

    //prints files contents or concatenate two files
    public void cat(String[] args) throws IOException {
        String FileName1, FileName2, ResultFile;

        if (args.length == 0) {
            System.out.println("These files do not exist");
        }
        if (args.length == 1) {
            FileName1 = args[0];
            File F = new File(FileName1);

            if (F.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(FileName1));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else System.out.println("File does not exist");
        }
        if (args.length > 1) {
            FileName1 = args[0];
            FileName2 = args[1];
            ResultFile = "Result.txt";

            File F1 = new File(FileName1);
            File F2 = new File(FileName2);

            if (F1.exists() && F2.exists()) {
                File NewFile = new File(ResultFile);
                NewFile.createNewFile();
                try {
                    BufferedReader reader1 = new BufferedReader(new FileReader(FileName1));
                    BufferedReader reader2 = new BufferedReader(new FileReader(FileName2));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(ResultFile, true));

                    String line;
                    while ((line = reader1.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    while ((line = reader2.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    reader1.close();
                    reader2.close();
                    writer.close();

                    System.out.println("Files have been concatenated to " + ResultFile);
                } catch (IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
            } else {
                System.out.println("The Files Do Not Exist");
            }
        }
    }

    //Displays number of lines, words and characters.
    public void wc(String[] args){
        String filename = args[0];
        int lineCount = 0;
        int wordCount = 0;
        int charCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();
                String[] words = line.split("\\s+");
                wordCount += words.length;
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        System.out.println(lineCount + " " + wordCount + " " + charCount + " " + filename);
    }

    //displays an enumerated list with the commands you’ve used in the past
    public void history() {
        if (command_History.isEmpty()) {
            System.out.println("Command history is empty.");
            return;
        }

        System.out.println("Command History is:");
        for (int h = 0; h < command_History.size(); h++) {
            System.out.println((h + 1) + " " + command_History.get(h));
        }
    }

    public void chooseCommand() throws IOException {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();
        Scanner scanner = new Scanner(System.in);

        switch (commandName) {
            case "pwd":
                pwd();
                break;
            case "cd":
                cd(args);
                break;
            case "ls":
                ls();
                break;
            case "ls -r":
                //listFilesReverse();
                break;
            case "echo":
                echo(args);
                break;
            case "cp":
                //cp();
                break;
            case "rm":
                rm(args);
                break;
            case "cat":
                cat(args);
            case "wc":
                wc(args);
            case "history":
                history();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                System.out.println("Command not valid");
        }
        command_History.add(commandName); // Add the executed command to the history
    }

    // The main function to execute program
    public static void main(String[] args) throws IOException {
        com.mycompany.terminal.Terminal terminal = new com.mycompany.terminal.Terminal();
        System.out.println("Choose The Command you need: ");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine(); //Enter command as an input

            if (input.equals("exit")) {        // if user enter or choose exit
                break;
            }

            if (terminal.parser.parse(input)) {  // if user enter or choose anything except exit .
                terminal.chooseCommand();
            } else {
                System.out.println("Invalid input"); //if user choose or enter invalid command
            }
        }
    }
}

