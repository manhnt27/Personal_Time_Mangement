package com.example.timemange;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Calendar;

public class CreateHabitPage extends AppCompatActivity {


    private LinearLayout habitColorSelectLiLayout;
    private FloatingActionButton habitColorSelectedButton;
    private TimeDatabaseHelper dbHelper;
    private static final String CHANNEL_ID = "Channel_Id";
    private int requestCode = 0;
    private String mode;
    private String name;
    private String time;
    private Habit receiverHabit = new Habit();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_habit_page);
        dbHelper = TimeDatabaseHelper.getInstance(this);

        //Log.i("Referenece",dbHelper.toString());
        TimePicker timePickerStart = (TimePicker) findViewById(R.id.habitStartTimePicker);
        TimePicker timePickerFinish = (TimePicker) findViewById(R.id.habitFinishTimePicker);
        Spinner habitCategorySpinner = (Spinner) findViewById(R.id.habitCategorySpinner);
        Button habitSaveButton = (Button) findViewById(R.id.habitSaveButton);
        Button habitDeleteButton = (Button) findViewById(R.id.habitDeleteButton);
        habitColorSelectLiLayout = (LinearLayout) findViewById(R.id.colorSelectLiLayout);
        habitColorSelectedButton = (FloatingActionButton) findViewById(R.id.habitColorSelectedButton);
        Spinner habitFreqSpinner = (Spinner) findViewById(R.id.habitFreqSpinner);
        EditText habitTitleEditText = (EditText) findViewById(R.id.habitTitleEditText);
        EditText habitNote = (EditText) findViewById(R.id.habitNote);
        EditText habitFreqEditText = (EditText) findViewById(R.id.habitFreqEditText);
        Button habitCloseAll = (Button) findViewById(R.id.habitCloseAll);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,R.array.category,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this,R.array.frequency,
                android.R.layout.simple_spinner_item);

        timePickerStart.setIs24HourView(true);
        timePickerFinish.setIs24HourView(true);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        habitCategorySpinner.setAdapter(categoryAdapter);
        habitFreqSpinner.setAdapter(freqAdapter);

        Intent intent = getIntent();
        mode = intent.getStringExtra("Configure_mode");

        if(mode.equals("Edit")){
            receiverHabit = (Habit) intent.getSerializableExtra("Activity_type");
            
            //Get the content from the selected card
            habitTitleEditText.setText(receiverHabit.getName());
            String timeStart = receiverHabit.getTime_start();
            timePickerStart.setHour(Integer.parseInt(timeStart.substring(11,13)));
            timePickerStart.setMinute(Integer.parseInt(timeStart.substring(14)));
            String timeFinish = receiverHabit.getTime_finish();
            timePickerFinish.setHour(Integer.parseInt(timeFinish.substring(11,13)));
            timePickerFinish.setMinute(Integer.parseInt(timeFinish.substring(14)));
            habitColorSelectedButton.setBackgroundTintList(ColorStateList.valueOf(receiverHabit.getColor()));
            habitCategorySpinner.setSelection(getPosFromSpinnerCategory(receiverHabit.getCategory()));
            String freq= receiverHabit.getFrequency();
            habitFreqEditText.setText(freq.substring(0, freq.indexOf('?')));
            habitFreqSpinner.setSelection(getPosFromSpinnerFreq(freq.substring(freq.indexOf('?')+1)));
            habitNote.setText((receiverHabit.getNote()==null) ? "" :receiverHabit.getNote());

            habitDeleteButton.setEnabled(true);
            habitDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteHabit(receiverHabit.getName(),receiverHabit.getTime_start());
                    Intent closeIntent = new Intent(CreateHabitPage.this, MainActivity.class);
                    closeIntent.putExtra("Update",true);
                    startActivity(closeIntent);
                }
            });

        }

        habitCloseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent closeIntent = new Intent(CreateHabitPage.this, MainActivity.class);
                closeIntent.putExtra("Update",true);
                startActivity(closeIntent);
            }
        });

        habitColorSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habitColorSelectLiLayout.setVisibility(View.VISIBLE);
            }
        });

        habitSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Habit habit = new Habit();


                int startHour = timePickerStart.getHour();
                int startMinute = timePickerStart.getMinute();
                LocalDate date = LocalDate.now();


                String timeStart = date.toString()+" "+TimeUtility.getTimeInForm(startHour)+":"
                        +TimeUtility.getTimeInForm(startMinute);


                int finishHour = timePickerFinish.getHour();
                int finishMinute = timePickerFinish.getMinute();
                String timeFinish = date.toString()+" "+TimeUtility.getTimeInForm(finishHour)+":"
                        + TimeUtility.getTimeInForm(finishMinute);

                String category = habitCategorySpinner.getSelectedItem().toString();

                habit.setName(habitTitleEditText.getText().toString());
                habit.setTime_start(timeStart);
                habit.setTime_finish(timeFinish);
                habit.setCategory(category);
                habit.setNote(habitNote.getText().toString());
                habit.setDone_times(0);

                String freq = habitFreqEditText.getText().toString()+"?"+habitFreqSpinner.getSelectedItem().toString();
                habit.setFrequency(freq);
                habit.setColor(habitColorSelectedButton.getBackgroundTintList().getDefaultColor());
                habit.setUser_ID(1);
                if(mode.equals("Edit")) {
                    dbHelper.updateHabit(receiverHabit.getName(), receiverHabit.getTime_start(), habit);
                    Toast.makeText(CreateHabitPage.this, "Activity has been updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    dbHelper.addHabit(habit);
                    Toast.makeText(CreateHabitPage.this, "A new activity has been added", Toast.LENGTH_SHORT).show();
                    createNotificationChannel();
                    scheduleNotification(habitTitleEditText.getText().toString(),
                            TimeUtility.getHourFromString(timeStart), TimeUtility.getMinuteFromString(timeStart), freq);
                }

            }
        });

    }
    public void habitCloseColorSelectList(View view){
        habitColorSelectedButton.setBackgroundTintList(view.getBackgroundTintList());
        habitColorSelectLiLayout.setVisibility(View.INVISIBLE);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Time Mange";
            String description = "Remind user of upcoming activity";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(String activityName,int hourStart,int minuteStart,String timeFreq) {//delay is after how much time(in millis) from current time you want to schedule the notification


        Intent intent = new Intent(CreateHabitPage.this, MyNotificationPublisher.class);
        intent.putExtra("Activity_Name",activityName);
        intent.putExtra("Activity_Type","Habit");
        int d = TimeUtility.getRequestCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateHabitPage.this,d, intent,0);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourStart);
        calendar.set(Calendar.MINUTE, minuteStart);

        int time = Integer.parseInt(timeFreq.substring(0, timeFreq.indexOf("?")));
        String freq = timeFreq.substring(timeFreq.indexOf("?")+1);

        if(freq.equals("Week"))
            time = time * 7;
        if(freq.equals("Month"))
            time = time * 30;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * time, pendingIntent);

    }

    private int getPosFromSpinnerFreq(String freq){
        if(freq.equals("Day"))
            return 0;
        else if(freq.equals("Week"))
            return 1;
        else return 2;
    }

    private int getPosFromSpinnerCategory(String category){
        if(category.equals("Leisure"))
            return 0;
        else if(category.equals("Health"))
            return 1;
        else if(category.equals("Work"))
            return 2;
        else if(category.equals("Family"))
            return 3;
        else if(category.equals("Education"))
            return 4;
        else if(category.equals("Sport"))
            return 5;
        else return 6;

    }






}