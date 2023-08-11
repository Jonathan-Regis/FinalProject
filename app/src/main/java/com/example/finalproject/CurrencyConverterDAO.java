package com.example.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalproject.CurrencyConverter;

import java.util.List;


@Dao
public interface CurrencyConverterDAO {
    @Insert
    void insertConversion(CurrencyConverter conversion);


    @Query("Select * from CurrencyConverter")
    List<CurrencyConverter> getAllConversions();


    @Delete
    void deleteConversion(CurrencyConverter conversion);

}
