package com.example.timemange;

public class Routine extends Activity{
    private int Priority;
    public Routine(String name, String time_start, String time_finish, String category, String note, int user_ID,int prioriry) {
        super(name, time_start, time_finish, category, note, user_ID);
        Priority = prioriry;
    }
    public Routine(){}

    public int getPriority() {
        return Priority;
    }

    public void setPrioriry(int priority) {
        Priority = priority;
    }
}