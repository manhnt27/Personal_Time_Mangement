package com.example.timemange;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.time.LocalDateTime;

public  class TimeUtility {

        private static int counter = 0;
        private static CalendarDay calendarDay;
        public static String getTimeInForm(int t){
            return (t > 9 ? String.valueOf(t) : ("0" +String.valueOf(t)));
        }

        public static String getDateInForm(int y,int m,int d){
            return getTimeInForm(y)+"-"+getTimeInForm(m)+"-"+getTimeInForm(d);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public static boolean checkFreq(LocalDate createdDate, String selectedDate, String timeFreq){
            int time = Integer.parseInt(timeFreq.substring(0, timeFreq.indexOf("?")));
            String freq = timeFreq.substring(timeFreq.indexOf("?")+1);
            LocalDate localSelectedDate = LocalDate.parse(selectedDate);
            if(localSelectedDate.compareTo(createdDate) == 0)
                return true;
            while(createdDate.compareTo(localSelectedDate) < 0){
                if(freq.equals("Day"))
                    createdDate = createdDate.plusDays(time);
                if(freq.equals("Week"))
                    createdDate = createdDate.plusWeeks(time);
                if(freq.equals("Month"))
                    createdDate = createdDate.plusMonths(time);
                if(localSelectedDate.compareTo(createdDate) == 0)
                    return true;
            }
            return false;

        }

    public static int getHourFromString(String time){
        return Integer.parseInt(time.substring(time.indexOf(' ')+1,time.indexOf(' ')+3));
    }

    public static int getMinuteFromString(String time){
        return Integer.parseInt(time.substring(time.indexOf(' ')+4,time.indexOf(' ')+6));
    }

    public static int getRequestCode(){
        return counter++;
    }

    public static CalendarDay getSelectedDay(){
            return calendarDay;
    }

    public static void setSelectedDate(CalendarDay selectedDate){
            calendarDay = selectedDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int numOfIntervalInHabit(LocalDateTime createdDate, String timeFreq){
        int time = Integer.parseInt(timeFreq.substring(0, timeFreq.indexOf("?")));
        String freq = timeFreq.substring(timeFreq.indexOf("?")+1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentDateTime = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),
                now.getHour(),now.getMinute());
        int cnt = 0;
        while(createdDate.compareTo(currentDateTime) <= 0){
            cnt++;
            if(freq.equals("Day"))
                createdDate = createdDate.plusDays(time);
            if(freq.equals("Week"))
                createdDate = createdDate.plusWeeks(time);
            if(freq.equals("Month"))
                createdDate = createdDate.plusMonths(time);
        }
        return cnt;
    }



}
