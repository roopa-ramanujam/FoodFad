package com.example.roopa.foodfad;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LongTermVisuals extends AppCompatActivity {
    public AppDatabase db;
    private static Button testdbButton;
    private List<NutritionInfo> all;
    private GraphView graph;
    private GraphView graphCarbs;
    private GraphView graphDailyCalGoal;

//    private LineChart chart;
    private ArrayList<Integer> calories = new ArrayList<Integer>();
    private ArrayList<Integer> carbs = new ArrayList<Integer>();;
    private ArrayList<Integer> protein = new ArrayList<Integer>();;
    private ArrayList<Integer> fat = new ArrayList<Integer>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_term_visuals);
        db = AppDatabase.getAppDatabase(getApplicationContext());
        initVars();
        all = db.nutritionInfoDao().getAll();
        graphStuff();

    }

    private void initVars() {
        Intent intent = getIntent();
    }


    private void graphStuff() {
        //DIFFERENT PACKAGE
        LineChart chart = (LineChart) findViewById(R.id.calschart);
        LineChart carbsChart = (LineChart) findViewById(R.id.carbschart);
        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> carbsEntries = new ArrayList<Entry>();
        int index = 1;
        int size = all.size();
        double cals = 0.0, proteinCals = 0.0, carbsCals = 0.0, fatCals = 0.0, proteinRatio = 0.0, carbsRatio = 0.0, fatRatio = 0.0, otherRatio = 0.0;
        float proteinPercent, carbsPercent, fatPercent, otherPercent;
        for (NutritionInfo item : all) {
            // turn your data into Entry objects
            entries.add(new Entry(index, item.getCalories()));
            carbsEntries.add(new Entry(index, item.getCarbohydrates()));
            if (index == size) {
                cals = item.getCalories();
                proteinCals = item.getProtein() * 4;
                carbsCals = item.getCarbohydrates() * 4;
                fatCals = item.getFat() * 9;
                proteinRatio = proteinCals / cals;
                carbsRatio = carbsCals / cals;
                fatRatio = fatCals / cals;
                otherRatio = 1 - proteinRatio - carbsRatio - fatRatio;
            }
            index++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineDataSet carbsDataSet = new LineDataSet(carbsEntries, "Label");
        LineData lineData = new LineData(dataSet);
        LineData carbsLineData = new LineData(carbsDataSet);
        chart.setData(lineData);
        carbsChart.setData(carbsLineData);
        chart.invalidate(); // refresh
        carbsChart.invalidate();

        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        List<PieEntry> yvalues = new ArrayList<PieEntry>();
        proteinPercent = (float)proteinRatio * 100;
        carbsPercent = (float)carbsRatio * 100;
        fatPercent = (float)fatRatio * 100;
        otherPercent = (float)otherRatio * 100;
        yvalues.add(new PieEntry(proteinPercent, "Protein"));
        yvalues.add(new PieEntry(carbsPercent, "Carbs"));
        yvalues.add(new PieEntry(fatPercent, "Fat"));
        //yvalues.add(new PieEntry(otherPercent, "Other"));

        PieDataSet pieDataSet = new PieDataSet(yvalues, "Breakdown of Last Meal");
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pdata = new PieData(pieDataSet);
        pdata.setValueFormatter(new PercentFormatter());
        pieChart.setData(pdata);

    }

}
