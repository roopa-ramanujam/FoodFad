package com.example.roopa.foodfad;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by roopa on 1/1/2018.
 */

@Dao
public interface NutritionInfoDao {

    @Query("SELECT * FROM nutritioninfo")
    List<NutritionInfo> getAll();

//    @Query("SELECT * FROM product WHERE name LIKE :name LIMIT 1")
//    NutritionInfo findByName(String name);

    @Insert
    long insertNewEntry(NutritionInfo item);

    @Update
    void update(NutritionInfo item);

    @Delete
    void delete(NutritionInfo item);


}