package com.example.timemange;

public class Event extends Activity{
    private String Time_notification;
    public Event(String name, String time_start, String time_finish, String category, String note, int user_ID,String time_notification) {
        super(name, time_start, time_finish, category, note, user_ID);
        Time_notification = time_notification;
    }

    public String getTime_notification() {
        return Time_notification;
    }

    public void setTime_notification(String time_notification) {
        Time_notification = time_notification;
    }

    public Event(){}
}