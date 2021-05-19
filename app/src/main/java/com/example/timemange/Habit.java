package com.example.timemange;

public class Habit  extends Activity{
    private int Done_times;

    private String Frequency;

    public Habit(){}

    public Habit(String name, String time_start, String time_finish, String category, String note, int user_ID,int done_times) {
        super(name, time_start, time_finish, category, note, user_ID);
        Done_times = done_times;
    }

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String frequency) {
        Frequency = frequency;
    }

    public int getDone_times() {
        return Done_times;
    }

    public void setDone_times(int done_times) {
        Done_times = done_times;
    }
}