package com.example.timemange;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import org.threeten.bp.LocalDate;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TimeDatabaseHelper dbHelper;
    private List<Activity> list = new ArrayList<>();
    private Map<RelativeLayout, Activity> cardMap = new HashMap<>();
    private LinearLayout cardLayout;
    private LayoutInflater inflater;
    private CalendarDay selectedCalendarDay;
    private boolean flag;
    private boolean isAllFabsVisible = false;
    private CalendarViewHelper calendarViewHelper;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCalendarView calendar = findViewById(R.id.calendarView);
        calendarViewHelper = new CalendarViewHelper(calendar);
        RelativeLayout mainRelLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        Button detailButton = findViewById(R.id.detail_button);
        ScrollView cardScrollView = findViewById(R.id.cardScrollView);
        RelativeLayout calendarLiLayout = findViewById(R.id.calendarLayout);
        FloatingActionButton addFAB = findViewById(R.id.addFAB);
        FloatingActionButton eventFAB = findViewById(R.id.eventFAB);
        FloatingActionButton habitFAB = findViewById(R.id.habitFAB);
        FloatingActionButton routineFAB = findViewById(R.id.routineFAB);
        RelativeLayout titleRelLayout = findViewById(R.id.titleRelLayout);


        //Log.i("Reference",calendar.toString());
        Intent intent = getIntent();
        CalendarDay calendarDay = CalendarDay.today();

        if(intent.getBooleanExtra("Update",false))
            calendarDay = TimeUtility.getSelectedDay();
        calendar.setSelectedDate(calendarDay);


        dbHelper = TimeDatabaseHelper.getInstance(this);
        cardLayout =(LinearLayout)findViewById(R.id.cardView);
        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addDot();
        getActivityFromDay(calendarDay,inflater);


        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                getActivityFromDay(date,inflater);
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(MainActivity.this,DetailPage.class);
                startActivity(detailIntent);
            }
        });

        AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
        alpha.setDuration(500); // Make animation instant
        alpha.setFillAfter(true);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFabsVisible) {

                    // Tell it to persist after the animation ends
// And then on your layout
                    cardScrollView.animate().alpha(0.2f).setDuration(150).start();
                    calendarLiLayout.animate().alpha(0.2f).setDuration(150).start();
                    titleRelLayout.animate().alpha(0.2f).setDuration(150).start();
                    routineFAB.show();
                    habitFAB.show();
                    eventFAB.show();

                    isAllFabsVisible = true;
                } else {
                    cardScrollView.animate().alpha(1f).setDuration(150).start();
                    calendarLiLayout.animate().alpha(1f).setDuration(150).start();
                    titleRelLayout.animate().alpha(1f).setDuration(150).start();
                    routineFAB.hide();
                    habitFAB.hide();
                    eventFAB.hide();
                    isAllFabsVisible = false;
                }
            }
        });

    }





    public void turnToRoutinePage(View view){
        Intent intent = new Intent(this, CreateRoutinePage.class);
        intent.putExtra("Configure_mode","Create");
        startActivity(intent);
    }

    public void turnToHabitPage(View view){
        Intent intent = new Intent(this, CreateHabitPage.class);
        intent.putExtra("Configure_mode","Create");
        startActivity(intent);
    }

    public void turnToEventPage(View view){
        Intent intent = new Intent(this, CreateEventPage.class);
        intent.putExtra("Configure_mode","Create");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getActivityFromDay(CalendarDay date,LayoutInflater inflater){
        TimeUtility.setSelectedDate(date);
        list.clear();
        cardMap.clear();
        cardLayout.removeAllViews();
        dbHelper.retrieveAllRoutine(list);
        //Log.i("Date",));
        String selectedDate = TimeUtility.getDateInForm(date.getYear(),date.getMonth(),date.getDay());
        dbHelper.retrieveHabit(list, selectedDate);
        dbHelper.retrieveEvent(list, selectedDate);
        Collections.sort(list);

        String prev="";

        for(Activity activity: list) {

            View v= inflater.inflate(R.layout.card_layout,null);
            TextView cardActivityTextView = v.findViewById(R.id.card_activities);
            TextView cardDateTextView = v.findViewById(R.id.card_date);

            RelativeLayout cardActivityRelLayout = v.findViewById(R.id.cardActivityRelLayout);
            cardMap.put(cardActivityRelLayout, activity);

            String start = (activity instanceof Event || activity instanceof Habit) ? activity.getTime_start().substring(11):
                    activity.getTime_start();
            String finish = (activity instanceof Event || activity instanceof Habit) ? activity.getTime_finish().substring(11):
                    activity.getTime_finish();

            if(!start.equals(prev)) {
                cardDateTextView.setText(start);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 40,0, 0);
                v.setLayoutParams(layoutParams);
            }

            if(activity instanceof Habit){
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.habit_checkbox);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((CheckBox) v).isChecked();
                        int done_times = ((Habit) activity).getDone_times();
                        if(checked){
                            ((Habit) activity).setDone_times(done_times+1);
                            dbHelper.updateHabit(activity.getName(),activity.getTime_start(),((Habit) activity));
                            flag = true;
                        } else {
                            if(done_times >0 && flag)
                            {
                                //Log.i("State",checked+"");
                                ((Habit) activity).setDone_times(done_times-1);
                                dbHelper.updateHabit(activity.getName(),activity.getTime_start(),((Habit) activity));
                                flag = false;
                            }
                        }
                    }
                });
            }

            cardActivityTextView.setText(activity.getName() + "\n"
                    +start+" - "+ finish  + "\n"+
                    ((activity.getNote()!=null) ? activity.getNote() : "\n"));
            cardActivityRelLayout.setBackgroundColor(activity.getColor());
            prev = start;
            cardLayout.addView(v);

            cardActivityRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity a = cardMap.get(cardActivityRelLayout);
                    //Event
                    if(a instanceof Event) {
                        Intent intent = new Intent(MainActivity.this,CreateEventPage.class);
                        intent.putExtra("Activity_type", a);
                        intent.putExtra("Configure_mode","Edit");
                        startActivity(intent);
                    }else
                    if(a instanceof Habit){
                        Intent intent = new Intent(MainActivity.this,CreateHabitPage.class);
                        intent.putExtra("Activity_type", a);
                        intent.putExtra("Configure_mode","Edit");
                        startActivity(intent);
                    }else
                    if(a instanceof Routine) {
                        Intent intent = new Intent(MainActivity.this,CreateRoutinePage.class);
                        intent.putExtra("Activity_type", a);
                        intent.putExtra("Configure_mode","Edit");
                        startActivity(intent);
                    }
                }
            });

        }
    }

    private void addDot(){
        List<Event> list = dbHelper.retrieveAllEvent();
        for(Event event: list){
            String time = event.getTime_start();
            int year = Integer.parseInt(time.substring(0,4));
            int month = Integer.parseInt(time.substring(5,7));
            int date = Integer.parseInt(time.substring(8,10));
            calendarViewHelper.addDotBelowDate(CalendarDay.from(year,month,date),event.getColor());
        }
    }





}