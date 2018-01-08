package com.example.roopa.foodfad;

import android.app.Application;
import android.content.Context;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.CYAN;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.YELLOW;
import static android.hardware.camera2.params.RggbChannelVector.RED;

public class LongTermVisuals extends AppCompatActivity {
    public AppDatabase db;
    private static Button testdbButton;
    private List<NutritionInfo> all;

//    private LineChart chart;
    private ArrayList<Integer> calories = new ArrayList<Integer>();
    private ArrayList<Integer> carbs = new ArrayList<Integer>();
    private ArrayList<Integer> protein = new ArrayList<Integer>();
    private ArrayList<Integer> fat = new ArrayList<Integer>();
    private Hashtable<Integer, String> indexToTimestamp = new Hashtable<Integer, String>();
    private int totalDailyCals = 0;
    private int totalDailyCarbs = 0;
    private int totalDailyProtein = 0;
    private int totalDailyFat = 0;

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

    private void makeDateHashMap() {
        int index = 1;
        for (NutritionInfo item : all) {
            indexToTimestamp.put(index, item.getTimestamp());
            index++;
        }
    }


    private void graphStuff() {
        //DIFFERENT PACKAGE
        makeDateHashMap();
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
            String dateString = item.getTimestamp();
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat targetFormat =  new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = originalFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedDate = targetFormat.format(date);

            Calendar c = Calendar.getInstance();
            String currentDate = targetFormat.format(c.getTime());

            if (formattedDate.equals(currentDate)) {
                totalDailyCals += item.getCalories();
                totalDailyCarbs += item.getCarbohydrates();
                totalDailyFat += item.getFat();
                totalDailyProtein += item.getProtein();
            }
            index++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineDataSet carbsDataSet = new LineDataSet(carbsEntries, "Label");
        LineData lineData = new LineData(dataSet);
        LineData carbsLineData = new LineData(carbsDataSet);

        XAxis xAxis = chart.getXAxis();
        //xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String timestamp = indexToTimestamp.get((int)value);
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat targetFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = null;
                try {
                    date = originalFormat.parse(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String formattedTimestamp = targetFormat.format(date);
                return formattedTimestamp;
            }
        });

        XAxis xAxisCarbs = carbsChart.getXAxis();
        //xAxis.setLabelRotationAngle(90);
        xAxisCarbs.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String timestamp = indexToTimestamp.get((int)value);
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat targetFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = null;
                try {
                    date = originalFormat.parse(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String formattedTimestamp = targetFormat.format(date);
                return formattedTimestamp;
            }
        });


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

        chart.getDescription().setEnabled(false);
        carbsChart.getDescription().setEnabled(false);
        pieChart.getDescription().setEnabled(false);

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> bargroup1 = new ArrayList<>();
        ArrayList<BarEntry> bargroup2 = new ArrayList<>();
        ArrayList<BarEntry> bargroup3 = new ArrayList<>();
        ArrayList<BarEntry> bargroup4 = new ArrayList<>();

        bargroup1.add(new BarEntry(0, 2000));
        bargroup1.add(new BarEntry(1, totalDailyCals));
        bargroup2.add(new BarEntry(2, 300));
        bargroup2.add(new BarEntry(3, totalDailyCarbs));
        bargroup3.add(new BarEntry(4, 65));
        bargroup3.add(new BarEntry(5, totalDailyFat));
        bargroup4.add(new BarEntry(6, 50));
        bargroup4.add(new BarEntry(7, totalDailyProtein));

// create BarEntry for Bar Group 1
//        ArrayList<BarEntry> bargroup2 = new ArrayList<>();
//        bargroup2.add(new BarEntry(6f, 0));
//        bargroup2.add(new BarEntry(10f, 1));
//        bargroup2.add(new BarEntry(5f, 2));
//        bargroup2.add(new BarEntry(25f, 3));
//        bargroup2.add(new BarEntry(4f, 4));
//        bargroup2.add(new BarEntry(17f, 5));


        List<IBarDataSet> bars = new ArrayList<IBarDataSet>();
// creating dataset for Bar Group1
        BarDataSet barDataSet1 = new BarDataSet(bargroup1, "Calories");
        BarDataSet barDataSet2 = new BarDataSet(bargroup2, "Carbs");
        BarDataSet barDataSet3 = new BarDataSet(bargroup3, "Fat");
        BarDataSet barDataSet4 = new BarDataSet(bargroup4, "Protein");


//barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet1.setColors(Color.BLUE);
        barDataSet2.setColors(Color.RED);
        barDataSet3.setColors(Color.MAGENTA);
        barDataSet4.setColors(Color.YELLOW);

        bars.add(barDataSet1);
        bars.add(barDataSet2);
        bars.add(barDataSet3);
        bars.add(barDataSet4);

// creating dataset for Bar Group 2
//        BarDataSet barDataSet2 = new BarDataSet(bargroup2, "Bar Group 2");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
//
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("2016");
//        labels.add("2015");
//        labels.add("2014");
//        labels.add("2013");
//        labels.add("2012");
//        labels.add("2011");
//
//        ArrayList<BarDataSet> dataSets = new ArrayList<>();  // combined all dataset into an arraylist
//        dataSets.add(barDataSet1);
//        dataSets.add(barDataSet2);

// initialize the Bardata with argument labels and dataSet
        BarData data = new BarData(bars);
        barChart.setData(data);

    }

}
