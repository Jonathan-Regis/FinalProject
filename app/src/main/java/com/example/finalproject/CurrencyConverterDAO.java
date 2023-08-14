package com.example.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalproject.CurrencyConverter;

import java.util.List;


/**
 * This is an interface that will help to insert and delete the conversions as well as display
 * the previous existing conversions
 */
@Dao
public interface CurrencyConverterDAO {
    @Insert
    void insertConversion(CurrencyConverter conversion);


    @Query("Select * from CurrencyConverter")
    List<CurrencyConverter> getAllConversions();


    @Delete
    void deleteConversion(CurrencyConverter conversion);

}
