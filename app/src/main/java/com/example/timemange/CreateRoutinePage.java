package com.example.timemange;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
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

import java.lang.reflect.Field;
import java.sql.Time;

public class CreateRoutinePage extends AppCompatActivity {

  
    private LinearLayout routineColorSelectLiLayout;
    private FloatingActionButton routineColorSelectedButton;
    private TimeDatabaseHelper dbHelper;
    private String mode;
    private String name;
    private String time;
    private Routine receiverRoutine = new Routine();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_routine_page);

        dbHelper = TimeDatabaseHelper.getInstance(this);

        //Log.i("Referenece",dbHelper.toString());
        TimePicker timePickerStart = (TimePicker) findViewById(R.id.routineStartTimePicker);
        TimePicker timePickerFinish = (TimePicker) findViewById(R.id.routineFinishTimePicker);
        Spinner routineCategorySpinner = (Spinner) findViewById(R.id.routineCategorySpinner);
        Spinner routinePrioritySpinner = (Spinner) findViewById(R.id.routinePrioritySpinner);
        EditText routineTitleEditText = (EditText) findViewById(R.id.routineTitleEditText);
        EditText routineNote = (EditText) findViewById(R.id.routineNote);
        Button routineSaveButton = (Button) findViewById(R.id.routineSaveButton);
        Button routineDeleteButton = (Button) findViewById(R.id.routineDeleteButton);
        routineColorSelectLiLayout = (LinearLayout) findViewById(R.id.colorSelectLiLayout);
        routineColorSelectedButton = (FloatingActionButton) findViewById(R.id.routineColorSelectedButton);
        Button routineCloseAll = (Button) findViewById(R.id.routineCloseAll);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,R.array.category,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,R.array.priority,
                android.R.layout.simple_spinner_item);

        timePickerStart.setIs24HourView(true);
        timePickerFinish.setIs24HourView(true);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routineCategorySpinner.setAdapter(categoryAdapter);
        routinePrioritySpinner.setAdapter(priorityAdapter);

        Intent intent = getIntent();
        mode = intent.getStringExtra("Configure_mode");
        if(mode.equals("Edit")){
            receiverRoutine = (Routine) intent.getSerializableExtra("Activity_type");
            
            //Get the content from the selected card
            routineTitleEditText.setText(receiverRoutine.getName());
            String timeStart = receiverRoutine.getTime_start();
            timePickerStart.setHour(Integer.parseInt(timeStart.substring(0,2)));
            timePickerStart.setMinute(Integer.parseInt(timeStart.substring(3)));
            String timeFinish = receiverRoutine.getTime_finish();
            timePickerFinish.setHour(Integer.parseInt(timeFinish.substring(0,2)));
            timePickerFinish.setMinute(Integer.parseInt(timeFinish.substring(3)));
            routineColorSelectedButton.setBackgroundTintList(ColorStateList.valueOf(receiverRoutine.getColor()));
            routineCategorySpinner.setSelection(getPosFromSpinnerCategory(receiverRoutine.getCategory()));
            routinePrioritySpinner.setSelection(((-receiverRoutine.getPriority() + 3) >= 0) ? (-receiverRoutine.getPriority() + 3) : 0 );
            routineNote.setText((receiverRoutine.getNote()==null) ? "" :receiverRoutine.getNote());

            routineDeleteButton.setEnabled(true);
            routineDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteRoutine(receiverRoutine.getName(),receiverRoutine.getTime_start());
                    Intent closeIntent = new Intent(CreateRoutinePage.this, MainActivity.class);
                    closeIntent.putExtra("Update",true);
                    startActivity(closeIntent);
                }
            });

        }



        //Choose color
        routineColorSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routineColorSelectLiLayout.setVisibility(View.VISIBLE);
            }
        });

        routineCloseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent closeIntent = new Intent(CreateRoutinePage.this, MainActivity.class);
                closeIntent.putExtra("Update",true);
                startActivity(closeIntent);
            }
        });

        //Save recent content
        routineSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Routine routine = new Routine();

                int startHour = timePickerStart.getHour();
                int startMinute = timePickerStart.getMinute();
                String timeStart = TimeUtility.getTimeInForm(startHour)+":"
                        +TimeUtility.getTimeInForm(startMinute);

                int finishHour = timePickerFinish.getHour();
                int finishMinute = timePickerFinish.getMinute();
                String timeFinish = TimeUtility.getTimeInForm(finishHour)+":"
                        +TimeUtility.getTimeInForm(finishMinute);

                String category = routineCategorySpinner.getSelectedItem().toString();
                String priority = routinePrioritySpinner.getSelectedItem().toString();

                int prior = 0;
                if(priority.equals("Low")) prior =3;
                if(priority.equals("Medium")) prior =2;
                if(priority.equals("High")) prior =1;
                //Log.i("Priority:",prior+"");
                routine.setName(routineTitleEditText.getText().toString());
                routine.setTime_start(timeStart);
                routine.setTime_finish(timeFinish);
                routine.setCategory(category);
                routine.setNote(routineNote.getText().toString());
                routine.setPrioriry(prior);
                //Log.i("COlor",routineColorSelectedButton.getBackgroundTintList().getDefaultColor()+"");
                routine.setColor(routineColorSelectedButton.getBackgroundTintList().getDefaultColor());
                routine.setUser_ID(1);
                if(mode.equals("Edit")) {
                    dbHelper.updateRoutine(receiverRoutine.getName(), receiverRoutine.getTime_start(), routine);
                    Toast.makeText(CreateRoutinePage.this, "Activity has been updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    dbHelper.addRoutine(routine);
                    Toast.makeText(CreateRoutinePage.this, "A new activity has been added", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //Close color selection area
    public void routineCloseColorSelectList(View view){
        routineColorSelectedButton.setBackgroundTintList(view.getBackgroundTintList());
        routineColorSelectLiLayout.setVisibility(View.INVISIBLE);
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