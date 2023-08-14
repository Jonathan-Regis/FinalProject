package com.example.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Getters and Setters for all components and for the database
 */
@Entity
public class CurrencyConverter {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "oldCurrency")
    public String beforeCurrency;

    @ColumnInfo(name = "startAmount")
    public String beforeAmount;

    @ColumnInfo(name = "newCurrency")
    public String convertedCurrency;

    @ColumnInfo(name = "newAmount")
    public String convertedAmount;

    @ColumnInfo(name = "TimeExecuted")
    public String timeExecuted;


    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getBeforeCurrency() {

        return beforeCurrency;
    }

    public void setBeforeCurrency(String beforeCurrency) {

        this.beforeCurrency = beforeCurrency;
    }

    public String getBeforeAmount() {

        return beforeAmount;
    }

    public void setBeforeAmount(String beforeAmount)
    {
        this.beforeAmount = beforeAmount;
    }

    public String getConvertedCurrency() {

        return convertedCurrency;
    }

    public void setConvertedCurrency(String convertedCurrency) {

        this.convertedCurrency = convertedCurrency;
    }

    public String getConvertedAmount() {

        return convertedAmount;
    }

    public void setConvertedAmount(String convertedAmount) {

        this.convertedAmount = convertedAmount;
    }

    public String getTimeExecuted()
    {

        return timeExecuted;
    }

    public void setTimeExecuted(String timeExecuted)
    {

        this.timeExecuted = timeExecuted;
    }
}