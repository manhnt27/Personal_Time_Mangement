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
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.lang.reflect.Field;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CreateEventPage extends AppCompatActivity {


    private LinearLayout eventColorSelectLiLayout;
    private FloatingActionButton eventColorSelectedButton;
    private TimeDatabaseHelper dbHelper;
    private static final String CHANNEL_ID = "Channel_Id";
    private String mode;
    private Event receiverEvent = new Event();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_page);
        dbHelper = TimeDatabaseHelper.getInstance(this);

        //Log.i("Referenece",dbHelper.toString());
        TimePicker timePickerStart = (TimePicker) findViewById(R.id.eventStartTimePicker);
        TimePicker timePickerFinish = (TimePicker) findViewById(R.id.eventFinishTimePicker);
        DatePicker datePickerStart = (DatePicker) findViewById(R.id.eventStartDatePicker);
        DatePicker datePickerFinish = (DatePicker) findViewById(R.id.eventFinishDatePicker);
        EditText eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
        EditText eventNote = (EditText) findViewById(R.id.eventNote);
        Spinner eventCategorySpinner = (Spinner) findViewById(R.id.eventCategorySpinner);
        Spinner eventNotificationSpinner = (Spinner) findViewById(R.id.eventNotificationSpinner);
        EditText eventNotificationEditText = (EditText) findViewById(R.id.eventNotificationEditText);
        Button eventSaveButton = (Button) findViewById(R.id.eventSaveButton);
        Button eventDeleteButton = (Button) findViewById(R.id.eventDeleteButton);
        eventColorSelectLiLayout = (LinearLayout) findViewById(R.id.colorSelectLiLayout);
        eventColorSelectedButton = (FloatingActionButton) findViewById(R.id.eventColorSelectedButton);
        Button eventCloseAll = (Button) findViewById(R.id.eventCLoseAll);


        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,R.array.category,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> notificationAdapter = ArrayAdapter.createFromResource(this,R.array.notification_time,
                android.R.layout.simple_spinner_item);
        timePickerStart.setIs24HourView(true);
        timePickerFinish.setIs24HourView(true);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        eventCategorySpinner.setAdapter(categoryAdapter);
        eventNotificationSpinner.setAdapter(notificationAdapter);

        Intent intent = getIntent();
        mode = intent.getStringExtra("Configure_mode");
        if(mode.equals("Edit")){
            receiverEvent = (Event) intent.getSerializableExtra("Activity_type");

            //Get the content from the selected card
            eventTitleEditText.setText(receiverEvent.getName());
            
            
            String timeStart = receiverEvent.getTime_start();
            timePickerStart.setHour(Integer.parseInt(timeStart.substring(11,13)));
            timePickerStart.setMinute(Integer.parseInt(timeStart.substring(14)));
            datePickerStart.updateDate(Integer.parseInt(timeStart.substring(0,4)), Integer.parseInt(timeStart.substring(5,7))-1,
                    Integer.parseInt(timeStart.substring(8,10)));

            String timeFinish = receiverEvent.getTime_finish();
            timePickerFinish.setHour(Integer.parseInt(timeFinish.substring(11,13)));
            timePickerFinish.setMinute(Integer.parseInt(timeFinish.substring(14)));
            datePickerFinish.updateDate(Integer.parseInt(timeFinish.substring(0,4)), Integer.parseInt(timeFinish.substring(5,7))-1,
                    Integer.parseInt(timeFinish.substring(8,10)));

            eventColorSelectedButton.setBackgroundTintList(ColorStateList.valueOf(receiverEvent.getColor()));
            eventCategorySpinner.setSelection(getPosFromSpinnerCategory(receiverEvent.getCategory()));
            eventNote.setText((receiverEvent.getNote()==null) ? "" :receiverEvent.getNote());
            String notifyTime= receiverEvent.getTime_notification();
            eventNotificationEditText.setText(notifyTime.substring(0, notifyTime.indexOf('?')));
            eventNotificationSpinner.setSelection(getPosFromSpinnerTimeNotification(notifyTime.substring(notifyTime.indexOf('?')+1)));

            eventDeleteButton.setEnabled(true);
            eventDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteEvent(receiverEvent.getName(),receiverEvent.getTime_start());
                    Intent closeIntent = new Intent(CreateEventPage.this, MainActivity.class);
                    closeIntent.putExtra("Update",true);
                    startActivity(closeIntent);
                }
            });
        }

        eventCloseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent closeIntent = new Intent(CreateEventPage.this, MainActivity.class);
                closeIntent.putExtra("Update",true);
                startActivity(closeIntent);
            }
        });
        
        eventColorSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventColorSelectLiLayout.setVisibility(View.VISIBLE);
            }
        });

        eventSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Event event = new Event();
                
                int startYear = datePickerStart.getYear();
                int startMonth = datePickerStart.getMonth()+1;
                int startDate = datePickerStart.getDayOfMonth();
                int startHour = timePickerStart.getHour();
                int startMinute = timePickerStart.getMinute();
                String timeStart = TimeUtility.getTimeInForm(startYear)+"-"
                    + TimeUtility.getTimeInForm(startMonth)+"-"
                    + TimeUtility.getTimeInForm(startDate)+" "
                    + TimeUtility.getTimeInForm(startHour)+":"
                        +TimeUtility.getTimeInForm(startMinute);
                
                int finishYear = datePickerStart.getYear();
                int finishMonth = datePickerStart.getMonth()+1;
                int finishDate = datePickerStart.getDayOfMonth();
                int finishHour = timePickerFinish.getHour();
                int finishMinute = timePickerFinish.getMinute();
                String timeFinish = TimeUtility.getTimeInForm(finishYear)+"-"
                        + TimeUtility.getTimeInForm(finishMonth)+"-"
                        + TimeUtility.getTimeInForm(finishDate)+" "
                        + TimeUtility.getTimeInForm(finishHour)+":"
                        +TimeUtility.getTimeInForm(finishMinute);

                String category = eventCategorySpinner.getSelectedItem().toString();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(timeStart,formatter);
                String notifyTimeType = eventNotificationSpinner.getSelectedItem().toString();
                int notifyTime = Integer.parseInt(eventNotificationEditText.getText().toString());

                if(notifyTimeType.equals("Minute"))
                    dateTime = dateTime.minusMinutes(notifyTime);
                if(notifyTimeType.equals("Hour"))
                    dateTime = dateTime.minusHours(notifyTime);
                if(notifyTimeType.equals("Day"))
                    dateTime = dateTime.minusDays(notifyTime);
                if(notifyTimeType.equals("Week"))
                    dateTime = dateTime.minusWeeks(notifyTime);
                //Log.i("Test",dateTime.format(formatter)+notifyTimeType);
                String eventName = eventTitleEditText.getText().toString();
                event.setName(eventName);
                event.setTime_start(timeStart);
                event.setTime_finish(timeFinish);
                event.setCategory(category);
                event.setNote(eventNote.getText().toString());
                event.setTime_notification(eventNotificationEditText.getText().toString()+"?"+eventNotificationSpinner.getSelectedItem());
                int color = eventColorSelectedButton.getBackgroundTintList().getDefaultColor();
                event.setColor(color);
                event.setUser_ID(1);
                if(mode.equals("Edit")) {

                    dbHelper.updateEvent(receiverEvent.getName(), receiverEvent.getTime_start(), event);
                    Toast.makeText(CreateEventPage.this, "Activity has been updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    dbHelper.addEvent(event);

                    Toast.makeText(CreateEventPage.this, "A new activity has been added", Toast.LENGTH_SHORT).show();
                    String startFinishEvent = TimeUtility.getTimeInForm(startHour) + ":"
                            + TimeUtility.getTimeInForm(startMinute) + " - " + TimeUtility.getTimeInForm(finishHour) + ":"
                            + TimeUtility.getTimeInForm(finishMinute);
                    createNotificationChannel();
                    scheduleNotification(eventName + " " + startFinishEvent, dateTime.getYear(), dateTime.getMonthValue(),
                            dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());
                }

            }
        });

    }
    public void eventCloseColorSelectList(View view){
        eventColorSelectedButton.setBackgroundTintList(view.getBackgroundTintList());
        eventColorSelectLiLayout.setVisibility(View.INVISIBLE);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scheduleNotification(String activityName, int yearStart, int monthStart, int dayStart,
                                      int hourStart, int minuteStart) {


        Intent intent = new Intent(CreateEventPage.this, MyNotificationPublisher.class);
        intent.putExtra("Activity_Name",activityName);
        intent.putExtra("Activity_Type","Event");
        int d = TimeUtility.getRequestCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateEventPage.this,d, intent,0);

        //Log.i("Counter",d+"");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, yearStart);
        calendar.set(Calendar.MONTH, monthStart-1);
        calendar.set(Calendar.DAY_OF_MONTH, dayStart);
        calendar.set(Calendar.HOUR_OF_DAY, hourStart);
        calendar.set(Calendar.MINUTE, minuteStart);
        //Log.i("Calendar",calendar.toString());

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

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

    private int getPosFromSpinnerTimeNotification(String timeNotify){
        if(timeNotify.equals("Minute"))
            return 0;
        else if(timeNotify.equals("Hour"))
            return 1;
        else if(timeNotify.equals("Day"))
            return 2;
        else return 3;
    }



}