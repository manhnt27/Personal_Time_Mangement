package com.example.timemange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private int counter = 0;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TimeMange";

    //table user
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID ="ID";
    private static final String COLUMN_USER_NAME ="Name";


    //table routine
    private static final String TABLE_ROUTINE = "Routine";
    private static final String COLUMN_ROUTINE_NAME ="Name";
    private static final String COLUMN_ROUTINE_TIME_START ="Time_start";
    private static final String COLUMN_ROUTINE_TIME_FINISH = "Time_finish";
    private static final String COLUMN_ROUTINE_CATEGORY ="Category";
    private static final String COLUMN_ROUTINE_NOTE ="Note";
    private static final String COLUMN_ROUTINE_PRIORITY ="Priority";
    private static final String COLUMN_ROUTINE_COLOR ="Color";
    private static final String COLUMN_ROUTINE_USERID = "User_ID";

    //table habit
    private static final String TABLE_HABIT = "Habit";
    private static final String COLUMN_HABIT_NAME ="Name";
    private static final String COLUMN_HABIT_TIME_START ="Time_start";
    private static final String COLUMN_HABIT_TIME_FINISH = "Time_finish";
    private static final String COLUMN_HABIT_CATEGORY ="Category";
    private static final String COLUMN_HABIT_NOTE ="Note";
    private static final String COLUMN_HABIT_DONE_TIMES ="Done_times";
    private static final String COLUMN_HABIT_FREQUENCY = "Frequency";
    private static final String COLUMN_HABIT_COLOR ="Color";
    private static final String COLUMN_HABIT_USERID = "User_ID";

    //table event
    private static final String TABLE_EVENT = "Event";
    private static final String COLUMN_EVENT_NAME ="Name";
    private static final String COLUMN_EVENT_TIME_START ="Time_start";
    private static final String COLUMN_EVENT_TIME_FINISH = "Time_finish";
    private static final String COLUMN_EVENT_CATEGORY ="Category";
    private static final String COLUMN_EVENT_NOTE ="Note";
    private static final String COLUMN_EVENT_TIME_NOTIFICATION ="Time_notification";
    private static final String COLUMN_EVENT_COLOR ="Color";
    private static final String COLUMN_EVENT_USERID = "User_ID";

    private static TimeDatabaseHelper instance;

    private TimeDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static synchronized TimeDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TimeDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String userScript = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_USER_NAME + " TEXT" + ")";
        db.execSQL(userScript);

        String routineScript = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTINE + "("
                + COLUMN_ROUTINE_NAME + " TEXT NOT NULL,"
                + COLUMN_ROUTINE_TIME_START+ " TEXT NOT NULL, "
                + COLUMN_ROUTINE_TIME_FINISH+ " TEXT NOT NULL, "
                + COLUMN_ROUTINE_CATEGORY+ " VARCHAR(10) NOT NULL, "
                + COLUMN_ROUTINE_NOTE+ " TEXT, "
                + COLUMN_ROUTINE_PRIORITY+ " INTEGER NOT NULL, "
                + COLUMN_ROUTINE_COLOR+ " INTEGER NOT NULL, "
                + COLUMN_ROUTINE_USERID+" INTEGER, "
                + "PRIMARY KEY " +"("+COLUMN_ROUTINE_NAME+","+COLUMN_ROUTINE_TIME_START+"), "
                + "FOREIGN KEY " + "("+COLUMN_ROUTINE_USERID +") "
                + "REFERENCES "+TABLE_USER+"("+COLUMN_USER_ID +") "
                + "ON DELETE CASCADE "
                + "ON UPDATE NO ACTION " +")";
        db.execSQL(routineScript);

        String habitScript = "CREATE TABLE IF NOT EXISTS " + TABLE_HABIT + "("
                + COLUMN_HABIT_NAME + " TEXT NOT NULL,"
                + COLUMN_HABIT_TIME_START+ " TEXT NOT NULL, "
                + COLUMN_HABIT_TIME_FINISH+ " TEXT NOT NULL, "
                + COLUMN_HABIT_CATEGORY+ " VARCHAR(10) NOT NULL, "
                + COLUMN_HABIT_NOTE+ " TEXT, "
                + COLUMN_HABIT_DONE_TIMES+ " INTEGER NOT NULL, "
                + COLUMN_HABIT_FREQUENCY + " TEXT NOT NULL, "
                + COLUMN_HABIT_COLOR+ " INTEGER NOT NULL, "
                + COLUMN_HABIT_USERID+" INTEGER, "
                + "PRIMARY KEY " +"("+COLUMN_HABIT_NAME+","+COLUMN_HABIT_TIME_START+"), "
                + "FOREIGN KEY " + "("+COLUMN_HABIT_USERID +") "
                + "REFERENCES "+TABLE_USER+"("+COLUMN_USER_ID +") "
                + "ON DELETE CASCADE "
                + "ON UPDATE NO ACTION " +")";
        db.execSQL(habitScript);

        String eventScript = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENT+ "("
                + COLUMN_EVENT_NAME + " TEXT NOT NULL,"
                + COLUMN_EVENT_TIME_START+ " TEXT NOT NULL, "
                + COLUMN_EVENT_TIME_FINISH+ " TEXT NOT NULL, "
                + COLUMN_EVENT_CATEGORY+ " VARCHAR(10), "
                + COLUMN_EVENT_NOTE+ " TEXT, "
                + COLUMN_EVENT_TIME_NOTIFICATION +" TEXT NOT NULL, "
                + COLUMN_HABIT_COLOR+ " INTEGER NOT NULL, "
                + COLUMN_EVENT_USERID+" INTEGER, "
                + "PRIMARY KEY " +"("+COLUMN_EVENT_NAME+","+COLUMN_EVENT_TIME_START+"), "
                + "FOREIGN KEY " + "("+COLUMN_EVENT_USERID +") "
                + "REFERENCES "+TABLE_USER+"("+COLUMN_USER_ID +") "
                + "ON DELETE CASCADE "
                + "ON UPDATE NO ACTION " +")";
        db.execSQL(eventScript);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addRoutine(Routine routine){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(COLUMN_ROUTINE_NAME,routine.getName());
        val.put(COLUMN_ROUTINE_TIME_START,routine.getTime_start());
        val.put(COLUMN_ROUTINE_TIME_FINISH,routine.getTime_finish());
        val.put(COLUMN_ROUTINE_CATEGORY,routine.getCategory());
        val.put(COLUMN_ROUTINE_NOTE,routine.getNote());
        val.put(COLUMN_ROUTINE_PRIORITY,routine.getPriority());
        val.put(COLUMN_ROUTINE_COLOR,routine.getColor());
        val.put(COLUMN_ROUTINE_USERID,routine.getUser_ID());

        db.insert(TABLE_ROUTINE,null,val);

    }

    public void addHabit(Habit habit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(COLUMN_HABIT_NAME,habit.getName());
        val.put(COLUMN_HABIT_TIME_START,habit.getTime_start());
        val.put(COLUMN_HABIT_TIME_FINISH,habit.getTime_finish());
        val.put(COLUMN_HABIT_CATEGORY,habit.getCategory());
        val.put(COLUMN_HABIT_NOTE,habit.getNote());
        val.put(COLUMN_HABIT_DONE_TIMES,habit.getDone_times());
        val.put(COLUMN_HABIT_COLOR,habit.getColor());
        val.put(COLUMN_HABIT_FREQUENCY,habit.getFrequency());
        val.put(COLUMN_HABIT_USERID,habit.getUser_ID());

        db.insert(TABLE_HABIT,null,val);

    }


    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(COLUMN_EVENT_NAME,event.getName());
        val.put(COLUMN_EVENT_TIME_START,event.getTime_start());
        val.put(COLUMN_EVENT_TIME_FINISH,event.getTime_finish());
        val.put(COLUMN_EVENT_CATEGORY,event.getCategory());
        val.put(COLUMN_EVENT_NOTE,event.getNote());
        val.put(COLUMN_EVENT_TIME_NOTIFICATION,event.getTime_notification());
        val.put(COLUMN_EVENT_COLOR,event.getColor());
        val.put(COLUMN_EVENT_USERID,event.getUser_ID());

        db.insert(TABLE_EVENT,null,val);

    }


    public void retrieveAll(){
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.i("Id: ",cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))+"");
                Log.i("Id: ",cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            } while (cursor.moveToNext());
        }
        ;
    }


    public void retrieveAllRoutine(List<Activity> list){
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTINE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Routine routine = new Routine();
                routine.setName(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_NAME)));
                routine.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_TIME_START)));
                routine.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_TIME_FINISH)));
                routine.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_CATEGORY)));
                routine.setPrioriry(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_PRIORITY)));
                routine.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_COLOR)));
                routine.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_NOTE)));
                routine.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_USERID)));
                list.add(routine);
            } while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void retrieveHabit(List<Activity> list, String selectedDate){
        String selectQuery = "SELECT  * FROM " + TABLE_HABIT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                String datetime = cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_START));
                datetime = datetime.substring(0, datetime.indexOf(' '));
                String timeFreq = cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_FREQUENCY));
                //Log.i("Databse",datetime+" "+timeFreq);
                if(!TimeUtility.checkFreq(LocalDate.parse(datetime),selectedDate,timeFreq))
                    continue;

                Habit habit = new Habit();
                habit.setName(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NAME)));
                habit.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_START)));
                habit.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_FINISH)));
                habit.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_CATEGORY)));
                habit.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_COLOR)));
                habit.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NOTE)));
                habit.setDone_times(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_DONE_TIMES)));
                habit.setFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_FREQUENCY)));
                habit.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_USERID)));
                list.add(habit);
            } while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();
    }

    public void retrieveEvent(List<Activity> list,String date){
        String selectQuery = "SELECT  * FROM " + TABLE_EVENT+" WHERE DATE("
                +COLUMN_EVENT_TIME_START +") = "+"'"+date+"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setName(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME)));
                event.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_START)));
                event.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_FINISH)));
                event.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_CATEGORY)));
                event.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_EVENT_COLOR)));
                event.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NOTE)));
                event.setTime_notification(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_NOTIFICATION)));
                event.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_EVENT_USERID)));
                list.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();
    }
    
    public Routine retrieveRoutine(String name,String time){
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTINE
                +" WHERE "+COLUMN_ROUTINE_TIME_START+" = '"+time+"'"
                +" AND "+ COLUMN_ROUTINE_NAME+" = '"+name+"'";
        Log.i("query",selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Routine routine = new Routine();
        if (cursor.moveToFirst()) {
            do {
                routine.setName(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_NAME)));
                Log.i("TimeDatabaseHelper",cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_NAME)));
                routine.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_TIME_START)));
                routine.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_TIME_FINISH)));
                routine.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_CATEGORY)));
                routine.setPrioriry(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_PRIORITY)));
                routine.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_COLOR)));
                routine.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_NOTE)));
                routine.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTINE_USERID)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return routine;
    }

    public void updateRoutine(String name,String time,Routine routine){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();

        val.put(COLUMN_ROUTINE_NAME,routine.getName());
        val.put(COLUMN_ROUTINE_TIME_START,routine.getTime_start());
        val.put(COLUMN_ROUTINE_TIME_FINISH,routine.getTime_finish());
        val.put(COLUMN_ROUTINE_CATEGORY,routine.getCategory());
        val.put(COLUMN_ROUTINE_NOTE,routine.getNote());
        val.put(COLUMN_ROUTINE_PRIORITY,routine.getPriority());
        val.put(COLUMN_ROUTINE_COLOR,routine.getColor());
        val.put(COLUMN_ROUTINE_USERID,routine.getUser_ID());


        String selection = COLUMN_ROUTINE_NAME + " = ? AND "+COLUMN_ROUTINE_TIME_START+" = ?";
        String[] selectionArgs = {name,time};

        db.update(TABLE_ROUTINE, val, selection, selectionArgs);

    }

    public Habit retrieveHabit(String name,String time){
        String selectQuery = "SELECT  * FROM " + TABLE_HABIT
                +" WHERE "+"strftime('%H:%M',"+COLUMN_HABIT_TIME_START+") = '"+time+"'"
                +" AND "+ COLUMN_HABIT_NAME+" = '"+name+"'";
        Log.i("query",selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Habit habit = new Habit();
        if (cursor.moveToFirst()) {
            do {
                habit.setName(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NAME)));
                habit.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_START)));
                habit.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_FINISH)));
                habit.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_CATEGORY)));
                habit.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_COLOR)));
                habit.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NOTE)));
                habit.setDone_times(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_DONE_TIMES)));
                habit.setFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_FREQUENCY)));
                habit.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_USERID)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return habit;
    }

    public void updateHabit(String name,String time,Habit habit){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();

        val.put(COLUMN_HABIT_NAME,habit.getName());
        val.put(COLUMN_HABIT_TIME_START,habit.getTime_start());
        val.put(COLUMN_HABIT_TIME_FINISH,habit.getTime_finish());
        val.put(COLUMN_HABIT_CATEGORY,habit.getCategory());
        val.put(COLUMN_HABIT_NOTE,habit.getNote());
        val.put(COLUMN_HABIT_DONE_TIMES,habit.getDone_times());
        val.put(COLUMN_HABIT_COLOR,habit.getColor());
        val.put(COLUMN_HABIT_FREQUENCY,habit.getFrequency());
        val.put(COLUMN_HABIT_USERID,habit.getUser_ID());


        String selection =  COLUMN_HABIT_NAME + " = ? AND " + COLUMN_HABIT_TIME_START+" = ?";
        String[] selectionArgs = {name,time};

        db.update(TABLE_HABIT, val, selection, selectionArgs);

    }

    public void updateEvent(String name,String time,Event event){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();

        val.put(COLUMN_EVENT_NAME,event.getName());
        val.put(COLUMN_EVENT_TIME_START,event.getTime_start());
        val.put(COLUMN_EVENT_TIME_FINISH,event.getTime_finish());
        val.put(COLUMN_EVENT_CATEGORY,event.getCategory());
        val.put(COLUMN_EVENT_NOTE,event.getNote());
        val.put(COLUMN_EVENT_TIME_NOTIFICATION,event.getTime_notification());
        val.put(COLUMN_EVENT_COLOR,event.getColor());
        val.put(COLUMN_EVENT_USERID,event.getUser_ID());


        String selection =  COLUMN_EVENT_NAME + " = ? AND " + COLUMN_EVENT_TIME_START+" = ?";
        String[] selectionArgs = {name,time};

        db.update(TABLE_EVENT, val, selection, selectionArgs);

    }
    
    public void deleteRoutine(String name,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ROUTINE_NAME + " = ? AND "+COLUMN_ROUTINE_TIME_START+" = ?";
        String[] selectionArgs = { name, time };
        db.delete(TABLE_ROUTINE, selection, selectionArgs);
    }

    public void deleteHabit(String name,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_HABIT_NAME + " = ? AND "+COLUMN_HABIT_TIME_START+" = ?";
        String[] selectionArgs = { name, time };
        db.delete(TABLE_HABIT, selection, selectionArgs);
    }

    public void deleteEvent(String name,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_EVENT_NAME + " = ? AND "+COLUMN_EVENT_TIME_START+" = ?";
        String[] selectionArgs = { name, time };
        db.delete(TABLE_EVENT, selection, selectionArgs);
    }

    public List<String> retrieveCategory(){
        List<String> list = new ArrayList<>();
        String selectRoutineQuery = "SELECT "+ COLUMN_ROUTINE_CATEGORY +" FROM " + TABLE_ROUTINE;
        String selectHabitQuery = "SELECT "+ COLUMN_HABIT_CATEGORY +" FROM " + TABLE_HABIT;
        String selectEventQuery = "SELECT "+ COLUMN_EVENT_CATEGORY +" FROM " + TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectRoutineQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(COLUMN_ROUTINE_CATEGORY)));
            } while (cursor.moveToNext());
        }

        cursor = db.rawQuery(selectHabitQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_CATEGORY)));
            } while (cursor.moveToNext());
        }

        cursor = db.rawQuery(selectEventQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_CATEGORY)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;

    }

    public List<Habit> retrieveAllHabit(){
        String selectQuery = "SELECT  * FROM " + TABLE_HABIT;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Habit> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Habit habit = new Habit();
                habit.setName(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NAME)));
                habit.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_START)));
                habit.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_TIME_FINISH)));
                habit.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_CATEGORY)));
                habit.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_COLOR)));
                habit.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NOTE)));
                habit.setDone_times(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_DONE_TIMES)));
                habit.setFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_FREQUENCY)));
                habit.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_HABIT_USERID)));

                list.add(habit);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Pair<String, Integer> retrieveUpcomingEvent(){

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String now = dateTime.format(formatter);
        String event = "None";

        String selectQuery = "SELECT  * FROM " + TABLE_EVENT+" WHERE "
                +COLUMN_EVENT_TIME_START +" > "+"'"+now+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                cnt++;
                if (cnt == 1) {
                   event = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME))+"  "
                            +cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_START)).substring(5,10)+" - "
                                +cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_FINISH)).substring(5,10);
                }
            } while (cursor.moveToNext());
        }

        Pair<String, Integer> pair = new Pair<>(event,cnt);
        return pair;
    }

    public List<Event> retrieveAllEvent(){
        String selectQuery = "SELECT  * FROM " + TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Event> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setName(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME)));
                event.setTime_start(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_START)));
                event.setTime_finish(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_FINISH)));
                event.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_CATEGORY)));
                event.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_EVENT_COLOR)));
                event.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NOTE)));
                event.setTime_notification(cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_TIME_NOTIFICATION)));
                event.setUser_ID(cursor.getInt(cursor.getColumnIndex(COLUMN_EVENT_USERID)));
                list.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

}
