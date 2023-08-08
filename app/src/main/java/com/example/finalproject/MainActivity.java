package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate( getLayoutInflater());
        setContentView(binding.getRoot());

        binding.TriviaGenerator.setOnClickListener(clk ->{
            Intent t = new Intent(this,TriviaActivity.class);
            startActivity(t);
        });

        binding.AviationTracker.setOnClickListener(clk ->{
            Intent a = new Intent(this,AviationActivity.class);
            startActivity(a);
        });

        binding.BearImage.setOnClickListener(clk ->{
            Intent b = new Intent(this,BearActivity.class);
            startActivity(b);
        });

        binding.CurrencyConverter.setOnClickListener(clk ->{
            Intent c = new Intent(this,CurrencyActivity.class);
            startActivity(c);
        });
    }
}