package com.example.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a currency conversion record stored in a database.
 */
@Entity
public class CurrencyConverter {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "Input Currency")
    public String inputCurrency;

    @ColumnInfo(name = "Input Amount")
    public String inputAmount;

    @ColumnInfo(name = "Output Currency")
    public String outputCurrency;

    @ColumnInfo(name = "Output Amount")
    public String outputAmount;

    @ColumnInfo(name = "Time Conversion Executed")
    public String timeExecuted;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInputCurrency() {
        return inputCurrency;
    }

    public void setInputCurrency(String inputCurrency) {
        this.inputCurrency = inputCurrency;
    }

    public String getInputAmount() {
        return inputAmount;
    }

    public void setInputAmount(String inputAmount) {
        this.inputAmount = inputAmount;
    }

    public String getOutputCurrency() {
        return outputCurrency;
    }

    public void setOutputCurrency(String outputCurrency) {
        this.outputCurrency = outputCurrency;
    }

    public String getOutputAmount() {
        return outputAmount;
    }

    public void setOutputAmount(String outputAmount) {
        this.outputAmount = outputAmount;
    }

    public String getTimeExecuted() {
        return timeExecuted;
    }

    public void setTimeExecuted(String timeExecuted) {
        this.timeExecuted = timeExecuted;
    }
}