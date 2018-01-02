package com.example.roopa.foodfad;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by roopa on 1/1/2018.
 */

@Entity
public class NutritionInfo {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    @TypeConverters({Converters.class})
    private String timestamp;

    @ColumnInfo(name = "calories")
    private int calories;

    @ColumnInfo(name = "carbs")
    private int carbohydrates;

    @ColumnInfo(name = "protein")
    private int protein;

    @ColumnInfo(name = "fat")
    private int fat;

    @ColumnInfo(name = "foodgroup")
    private String foodgroup;
    // getters and setters


    public void setId(long i) {
        id = i;
    }

    public void setTimestamp(String ts) {
        timestamp = ts;
    }

    public void setCalories(int cals) {
        calories = cals;
    }

    public void setCarbohydrates(int carbs) {
        carbohydrates = carbs;
    }

    public void setProtein(int p) {
        protein = p;
    }

    public void setFat(int f) {
        fat = f;
    }

    public void setFoodgroup(String fg) {
        foodgroup = fg;
    }

    public long getId() {
            return id;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public int getCalories() {
        return calories;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }


    public int getProtein() {
        return protein;
    }


    public int getFat() {
        return fat;
    }

    public String getFoodgroup() {
        return foodgroup;
    }
}