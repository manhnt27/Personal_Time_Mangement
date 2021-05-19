package com.example.timemange;

import android.graphics.Color;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class CalendarViewHelper {
    private MaterialCalendarView materialCalendarView;

    public CalendarViewHelper(MaterialCalendarView calendar){
        materialCalendarView = calendar;
    }
    public void addDotBelowDate(CalendarDay calendarDay, int color){
        CurrentDayDecorator decorator = new CurrentDayDecorator(calendarDay, color);
        //Log.i("Caldendar",materialCalendarView.toString());
        materialCalendarView.addDecorator(decorator);
    }

    public class CurrentDayDecorator implements DayViewDecorator {
        private final int color;
        private final CalendarDay day;

        public CurrentDayDecorator(CalendarDay day, int color) {
            this.color = color;
            this.day = day;
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            if (this.day.equals(day)){
                //Log.i("Test","Tested");
                return true;
            }
            return false;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(7,color));
        }
    }
}
