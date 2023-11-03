package com.mycompany.terminal;

import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


public class Terminal {
    private Parser parser ;
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

    // list directories and files reverse
    public void ls_r() {
        File current_Directory = new File(System.getProperty("user.dir"));
        File[] contents = current_Directory.listFiles();
        if (contents != null) {
            Arrays.sort(contents); // Sort files and directories alphabetically
            for (int i = contents.length - 1; i >= 0; i--) {
                System.out.println(contents[i].getName());
            }
        }
    }

    //Takes 2 arguments, both are files and copies the first onto the second.
    public void cp(String [] files) {
        try (FileInputStream sourceStream = new FileInputStream(files[0]);
             FileOutputStream targetStream = new FileOutputStream(files[1])) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = sourceStream.read(buffer)) != -1) {
                targetStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Takes 2 arguments, both are directories (empty or not) and copies 
    //the first directory (with all its content) into the second one.
    //not compliter
    public void cp_r(){

    }


    public void touch(String[] filePath){
        File file = new File(filePath[0]);
        try{
            if (file.createNewFile()) {
                System.out.println("File created successfully: " + filePath);
            } else {
                System.out.println("File already exists: " + filePath);
            }
        } catch(IOException e){
            e.printStackTrace();
            System.err.println("Failed to create the file: " + filePath);
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



    public void mkdir(String directoryNames[]){
        for(String directoryName : directoryNames){
            File directory = new File(directoryName);
            
            //if user writr the full path of the directory
            if (directory.isAbsolute()) {
                if (directory.mkdirs()) {
                    System.out.println("Directory created successfully: " + directoryName);
                } else {
                    System.out.println("Failed to create the directory: " + directoryName);
                }
            }

            //if user write the name of directory only create it in the currently path
            else {
                File currentDir = new File(System.getProperty("user.dir"));
                File newDirectory = new File(currentDir, directoryName);

                if (newDirectory.mkdirs()) {
                    System.out.println("Directory created successfully: " + newDirectory.getAbsolutePath());
                } else {
                    System.out.println("Failed to create the directory: " + newDirectory.getAbsolutePath());
                }
            }

        }
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

  public void chooseCommand() {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();
       
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
            case "echo":
                echo(args);
                break;    
            case "cp":  
                cp(args);
                break;   
            case "touch":
                touch(args);
                break;
            case "mkdir":
                mkdir(args);
                break;
            case "history":
                history();
                break; 
            case "ls-r":
                ls_r();
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
  public static void main(String[] args) { 
        Terminal terminal = new Terminal();
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
