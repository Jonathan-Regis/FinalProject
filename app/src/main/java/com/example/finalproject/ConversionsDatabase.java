package com.example.finalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CurrencyConverter.class}, version = 1)
public abstract class ConversionsDatabase extends RoomDatabase {

    public abstract CurrencyConverterDAO ccDAO();
}