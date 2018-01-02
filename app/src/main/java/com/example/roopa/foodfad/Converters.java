package com.example.roopa.foodfad;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * Created by roopa on 1/1/2018.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}