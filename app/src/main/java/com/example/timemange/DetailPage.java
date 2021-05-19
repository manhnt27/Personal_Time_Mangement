package com.example.timemange;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailPage extends AppCompatActivity {
    private TimeDatabaseHelper dbHelper;
    private List<Habit> missingHabitList = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        dbHelper = TimeDatabaseHelper.getInstance(this);
        PieChart pieChart = (PieChart) findViewById(R.id.detail_piechart);
        BarChart barChart = (BarChart) findViewById(R.id.stackedBarChart);
        TextView upcomingEventTextView = (TextView) findViewById(R.id.upcoming_event);
        TextView nextEventTextView = (TextView) findViewById(R.id.next_event);
        TextView missingHabitTextView = (TextView) findViewById(R.id.missing_habit);

        Pair<String, Integer> pair = dbHelper.retrieveUpcomingEvent();
        upcomingEventTextView.setText(pair.first);
        nextEventTextView.setText(pair.second+"");

        setupPieChart(pieChart);
        loadPieChartData(pieChart);
        setUpStackedBarChart(barChart);
        loadStackedBarChartData(barChart);

        for(Habit missingHabit: missingHabitList)
            missingHabitTextView.setText(missingHabit.getName()+"\n");
    }

    private void setupPieChart(PieChart pieChart) {
        pieChart.setDrawHoleEnabled(false);


        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);

        l.setEnabled(true);
    }

    private void loadPieChartData(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        List<String> categoryList = dbHelper.retrieveCategory();
        Map<String, Integer> categoryMap = new HashMap<>();
        for(String category: categoryList)
            if(categoryMap.containsKey(category)){
                categoryMap.put(category,categoryMap.get(category)+1);
            } else
                categoryMap.put(category,1);
        int total = 0;
        for(Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            total += entry.getValue();
        }
        int size = categoryMap.size();
        float categoryPercent[] = new float[size];
        float currentSum = 0f;
        int idx = 0;
        for(Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            if(idx == size - 1)
                categoryPercent[idx] = 1-currentSum;
            else{
                categoryPercent[idx] = Math.round( (((float)entry.getValue()) /((float) total)) * 100.00f) / 100.00f;
                currentSum += categoryPercent[idx];
                idx++;
            }
        }

        idx = 0;
        for(Map.Entry<String, Integer> stringIntegerEntry : categoryMap.entrySet()){
            entries.add(new PieEntry(categoryPercent[idx],stringIntegerEntry.getKey()));
            idx++;
        }


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setUpStackedBarChart(BarChart bar){
        bar.setDrawBarShadow(false);
        bar.setDrawValueAboveBar(false);
        bar.setDrawGridBackground(false);
        bar.getDescription().setEnabled(false);

        bar.animateY(1400,Easing.EaseInExpo);

        Legend legend = bar.getLegend();
        legend.setFormLineWidth(2f);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadStackedBarChartData(BarChart bar){
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        List<Habit> habitList = dbHelper.retrieveAllHabit();
        int idx = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //LocalDateTime date = LocalDateTime.parse("2021-05-06 07:54",formatter);
        String[] labels = new String[habitList.size()+1];
        labels[0] = "dummy";
        for(Habit habit: habitList){
            LocalDateTime createdDate = LocalDateTime.parse(habit.getTime_start(),formatter);
            labels[idx] = habit.getName();
            Log.i("Label",idx+"");
            int missingTimes = TimeUtility.numOfIntervalInHabit(createdDate,habit.getFrequency())
                    - habit.getDone_times();
            if(missingTimes > 0)
                missingHabitList.add(habit);
            barEntries.add(new BarEntry(idx,new float[]{habit.getDone_times(),missingTimes}));
            idx++;
        }


        BarDataSet barDataSet=new BarDataSet(barEntries,"Habit's done times");

        barDataSet.setStackLabels(new String[]{"Done","Missing"});
        barDataSet.setColors(new int[]{Color.BLUE,Color.RED});        //Set the color for the first group of columns

        BarData barData = new BarData(barDataSet);


        bar.setData(barData);
        barData.setBarWidth(0.2f);

        XAxis x = bar.getXAxis();
        x.setDrawGridLines(false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);

        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        //x.setGranularity(0.2f);
        //x.setGranularityEnabled(true);

        YAxis yLabels = bar.getAxisLeft();
        yLabels.setDrawGridLines(false);
        YAxis yLabels1 = bar.getAxisRight();
        yLabels1.setEnabled(false);
        yLabels.setGranularity(1.0f);
        yLabels.setGranularityEnabled(true); // Required to enable granularity

    }


}