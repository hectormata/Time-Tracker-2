package com.main.java.task;

import com.main.java.task.Task.TaskList;

import java.util.Scanner;
import java.util.Calendar;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

class TaskManager{
    private TaskList tl;


    public TaskManager() {
        tl = new TaskList(getSaveFile());
    }

    public TaskList getTl() {
        return tl;
    }

    public String getSaveFile() {
        try {
            File file = new File("file.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            if(line != null) {
                System.out.println("Save file location is " + line);
                return line;
            }
        }
        catch (Exception e) {
            System.err.println("There was an error finding the save file");
        }

        return "";
    }

    //Array holding all the commands the user can enter
    public String[][] cmds = {
            {"help", "print a list of possible commands"},
            {"add", "add a task to the task list"},
            {"edit", "edit a task already in the task list"},
            {"delete", "delete a task from the task list"},
            {"list", "list all tasks in the task list"},
            {"now", "print the highest priority task"},
            {"q", "exits the program"}
    };

    //Print out the list of commands users can enter
    public void printCmds(){
        System.out.println("\nCommands:");
        for(int i = 0; i < cmds.length; i++) {
            System.out.println("\t'" + cmds[i][0] + "':\t" + cmds[i][1]);
        }
    }

    //Prompt the user to enter a command
    //Return the entered command
    public String promptUserCmd(Scanner sc){
        System.out.println("\nPlease enter a command");
        System.out.println("Enter 'help' for a list of commands");
        return sc.nextLine();
    }

    //Parse date string and return Calendar set to that date
    //return Calendar
    public Calendar parseDate(String dateStr, Calendar date){
        String[] strs = dateStr.split("/");
        if(strs.length < 3){
            return null;
        }

        int month, day, year;
        try{
            month = Integer.parseInt(strs[0]) - 1;
            day = Integer.parseInt(strs[1]);
            year = Integer.parseInt(strs[2]);
        }catch(NumberFormatException e){
            return null;
        }

        if(year < 100)
        {
            year += 2000;
        }
        date.set(year, month, day);
        return date;
    }

    //Parse time string and return Calendar set to that time
    //return Calendar
    public Calendar parseTime(String timeStr, Calendar date){
        String[] strs = timeStr.split(":");
        if(strs.length < 2){
            return null;
        }

        int[] ints = new int[3];
        try{
            ints[0] = Integer.parseInt(strs[0]);
            ints[1] = Integer.parseInt(strs[1]);
        }catch(NumberFormatException e){
            return null;
        }

        date.set(Calendar.HOUR_OF_DAY, ints[0]);
        date.set(Calendar.MINUTE, ints[1]);
        return date;
    }

    //Prompt user for date and time
    //Return Calendar with that information
    public Calendar promptUserDeadln(Scanner sc){
        Calendar date = Calendar.getInstance();
        System.out.println("Enter deadline date of task (mm/dd/yyyy or mm/dd/yy)\nor enter 'c'to cancel");
        String dateStr = sc.nextLine();
        if(dateStr.equals("c"))
            return null;

        date = parseDate(dateStr, date);

        System.out.println("Enter deadline time of task (hh:mm)\nor enter 'c' to cancel");
        String timeStr = sc.nextLine();
        if(timeStr.equals("c"))
            return null;

        date = parseTime(timeStr, date);

        return date;
    }

    //Prompt user for estimated hours to complete
    //Return string value
    public int promptUserComplHrs(Scanner sc){
        String hrsStr = "";
        int hrs = -1;
        while(hrsStr == null || hrsStr.equals("") || hrsStr.equals(" ")){
            System.out.println("Enter estimated hours to complete task\nor enter 'c' to cancel");
            hrsStr = sc.nextLine();

            if(hrsStr.equals("c"))
                return -1;

            try{
                hrs = Integer.parseInt(hrsStr);
                if(hrs < 0)
                    hrs = -1;
            }catch(NumberFormatException e){
                hrs = -1;
            }
        }
        return hrs;
    }

    //Prompt user for a description
    //Return description
    public String promptUserDesc(Scanner sc){
        String desc = "";
        while(desc == null || desc.equals("") || desc.equals(" ")){
            System.out.println("Enter description of task\nor enter 'c' to cancel");
            desc = sc.nextLine();

            if(desc == null || desc.equals("") || desc.equals(" ")){
                System.err.println("ERROR: description invalid");
            }
        }
        return desc;
    }

    //Prompt user for all the information needed to add a task to TaskList
    //Return true if successful
    public boolean userAddTask(Scanner sc){
        String desc = promptUserDesc(sc);
        if(desc.equals("c") || desc == null)
            return false;


        Calendar date = promptUserDeadln(sc);
        if(date == null)
            return false;

        int complHrs = promptUserComplHrs(sc);
        if(complHrs < 0)
            return false;

        if(desc != null && date != null && complHrs >= 0){
            tl.addTask(desc, date, complHrs);
            return true;
        }
        return false;
    }

    //prompts the user for a task ID
    //returns the ID when the user enters a valid ID
    public int promptUserTaskId(Scanner sc){
        int id = -1;
        while(id == -1){
            try{
                System.out.println("Enter a task ID\nor enter 'c' to cancel");
                String intId = sc.nextLine();
                if(intId.equals("c"))
                    return -1;
                id = Integer.parseInt(intId);
            }
            catch(NumberFormatException e){
                id = -1;
                continue;
            }
            if(!tl.inTaskList(id)){
                id = -1;
                continue;
            }
        }
        return id;
    }

    //prompts user for which part of the task they want to edit
    //returns the string they enter
    public String promptUserEditCmd(Scanner sc){
        String editCmd = "";
        System.out.println("What do you want to edit?");

        while(editCmd.equals("")){
            System.out.println("Please enter 'description', 'deadline', 'completion hours', or 'c' to cancel");
            editCmd = sc.nextLine();

            if(editCmd.equals("description") || editCmd.equals("deadline") || editCmd.equals("completion hours") || editCmd.equals("c")){
                return editCmd;
            }else{
                editCmd = "";
            }
        }
        return editCmd;
    }

    //prompt the user for all information needed to edit a task
    public boolean userEditTask(Scanner sc){
        if(tl.size() <= 0){
            System.out.println("No tasks in task list");
            return false;
        }

        int id = promptUserTaskId(sc);
        if(id == -1)
            return false;

        String editCmd = promptUserEditCmd(sc);
        switch (editCmd){
            case "description":
                String desc = promptUserDesc(sc);
                if(desc.equals("c"))
                    return false;
                else
                    tl.editTask(id, desc);
                break;
            case "deadline":
                tl.editTask(id, promptUserDeadln(sc));
                break;
            case "completion hours":
                tl.editTask(id, promptUserComplHrs(sc));
                break;
            case "c":
                return false;
            default:
                return false;
        }
        return true;
    }

    //prompt the user for a task to delete
    public boolean userDeleteTask(Scanner sc){
        if(tl.size() <= 0){
            System.out.println("No tasks in task list");
            return false;
        }

        int taskId = promptUserTaskId(sc);
        if(tl.deleteTask(taskId) && taskId >= 0)
            return true;
        return false;
    }

    //main function
    //loops until user enters "q" to quit the program
    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        Scanner sc = new Scanner(System.in);

        boolean keepRunning = true;

        while(keepRunning){
            String userCmd = tm.promptUserCmd(sc);
            switch(userCmd){
                case "help":
                    tm.printCmds();
                    break;
                case "add":
                    if(tm.userAddTask(sc))
                        System.out.println("Task added successfully");
                    else
                        System.err.println("There was an error adding the task");
                    break;
                case "edit":
                    if(tm.userEditTask(sc))
                        System.out.println("Task edited successfully");
                    else
                        System.out.println("There was an error editing the task");
                    break;
                case "list":
                    System.out.println(tm.getTl());
                    break;
                case "now":
                    System.out.println(tm.getTl().now());
                    break;
                case "delete":
                    if(tm.userDeleteTask(sc))
                        System.out.println("Task deleted successfully");
                    else
                        System.out.println("There was an error deleting the task");
                    break;
                case "q":
                    System.out.println("Exiting");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Command not recognized");
                    break;
            }
        }
        sc.close();
    }
}