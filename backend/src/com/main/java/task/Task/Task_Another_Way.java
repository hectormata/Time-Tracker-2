package com.main.java.task.Task;

import java.util.Calendar;

public class Task_Another_Way {

    private int id; //identifier for the object
    private String desc; //description of the task
    private Calendar deadln; //deadline
    private int complHrs; //hours to completion
    private int priority; //priority level calculated based on deadline and hours to completion

    //Constructor
    //Creates a task with the information entered
    public Task_Another_Way(int id, String desc, Calendar deadln, int complHrs){
        this.id = id;
        this.desc = desc;
        this.deadln = deadln;
        this.complHrs = complHrs;
        calcPriority();
    }


    //calculates the priority of the task based on deadline date and hours it will take to complete
    private void calcPriority(){
        if(deadln == null){
            priority = -1;
            return;
        }
        Calendar currDate = Calendar.getInstance();

        int diffYr = deadln.get(Calendar.YEAR) - currDate.get(Calendar.YEAR);
        int diffMnth = deadln.get(Calendar.MONTH) - currDate.get(Calendar.MONTH);
        int diffDay = deadln.get(Calendar.DAY_OF_MONTH) - currDate.get(Calendar.DAY_OF_MONTH);
        int diffHr = deadln.get(Calendar.HOUR_OF_DAY) - currDate.get(Calendar.HOUR_OF_DAY);
        int diffMin = deadln.get(Calendar.MINUTE) - currDate.get(Calendar.MINUTE);

        if(diffMin < 0){
            diffMin += 60;
            diffHr--;
        }

        if(diffHr < 0){
            diffHr += 24;
            diffDay--;
        }

        if(diffDay < 0){
            diffDay += 31;
            diffMnth--;
        }

        if(diffMnth < 0){
            diffMnth += 12;
            diffYr--;
        }

        if(diffYr < 0){
            priority = -1;
        }

        diffHr += diffDay * 24;
        diffHr += diffMnth * 720;
        diffHr += diffYr * 8640;

        priority = (complHrs / diffHr) * 100;
    }

    //Getter
    //returns the id value
    public int getId(){
        return id;
    }

    //Setter
    //sets the description
    public void setDesc(String newDesc){
        desc = newDesc;
    }

    //Getter
    //returns the description
    public String getDesc(){
        return desc;
    }

    //Setter
    //sets deadline
    public void setDeadln(Calendar newDeadln){
        deadln = newDeadln;
    }

    //Getter
    //returns deadline
    public Calendar getDeadln(){
        return deadln;
    }

    //Setter
    //sets hours to completion
    public void setComplHrs(int newComplHrs){
        complHrs = newComplHrs;
    }

    //Getter
    //returns hours to completion
    public int getComplHrs(){
        return complHrs;
    }

    //Getter
    //returns priority of the task
    public int getPriority(){
        return priority;
    }

    //returns a string of all the important information
    @Override
    public String toString(){
        return
                "id: " + id + "\tTask Description: " + desc
                        +
                        "\n\t Due: " + deadln.getTime() + "\tCompletion Hours: " + complHrs;
    }

}
