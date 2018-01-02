package com.example.roopa.foodfad;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

/**
 * Created by roopa on 1/1/2018.
 */
@TypeConverters({Converters.class})
@Database(entities = {NutritionInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase db;
    public abstract NutritionInfoDao nutritionInfoDao();
    public static AppDatabase getAppDatabase(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "NutritionDB")
                    // don't do this on a real app!
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
    public static void destroyInstance() {
        db = null;
    }
}
