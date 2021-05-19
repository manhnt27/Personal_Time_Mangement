package com.example.timemange;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;

public abstract class Activity implements Comparable<Activity>, Serializable {
    private String Name;
    private String Time_start;
    private String Time_finish;
    private String Category;
    private String Note;
    private int User_ID;
    private int Color;

    public Activity(){}
    public Activity(String name, String time_start, String time_finish, String category, String note, int user_ID) {
        Name = name;
        Time_start = time_start;
        Time_finish = time_finish;
        Category = category;
        Note = note;
        User_ID = user_ID;
    }

    public String getName() {
        return Name;
    }

    public String getTime_start() {
        return Time_start;
    }

    public String getTime_finish() {
        return Time_finish;
    }

    public String getCategory() {
        return Category;
    }

    public String getNote() {
        return Note;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setTime_start(String time_start) {
        Time_start = time_start;
    }

    public void setTime_finish(String time_finish) {
        Time_finish = time_finish;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(Activity other) {
        LocalTime time1, time2;
        if(this instanceof Event || this instanceof Habit)
            time1 = LocalTime.parse(this.getTime_start().substring(11));
        else time1 = LocalTime.parse(this.getTime_start());


        if(other instanceof Event || other instanceof Habit)
            time2 = LocalTime.parse(other.getTime_start().substring(11));
        else time2 = LocalTime.parse(other.getTime_start());


        if(time1.compareTo(time2) == 0){
            Integer order1=0, order2=0;
            if(this instanceof Routine) order1=3;
            if(this instanceof Habit) order1=2;
            if(this instanceof Event) order1=1;

            if(other instanceof Routine) order2=3;
            if(other instanceof Habit) order2=2;
            if(other instanceof Event) order2=1;

            if(order1 == order2 && order1 ==3)
                return Integer.compare(((Routine) this).getPriority(),((Routine) other).getPriority());
            return Integer.compare(order1,order2);
        }
        return time1.compareTo(time2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name,Time_start);
    }
}