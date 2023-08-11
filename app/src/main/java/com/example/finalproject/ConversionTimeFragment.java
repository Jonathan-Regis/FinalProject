package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finalproject.databinding.TimeLayoutBinding;

public class ConversionTimeFragment extends Fragment {

    CurrencyConverter currencyConverter;

    public ConversionTimeFragment(CurrencyConverter cc){currencyConverter = cc;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        TimeLayoutBinding binding = TimeLayoutBinding.inflate(inflater);

        binding.timeExecTextView.setText("  " + currencyConverter.timeExecuted);

        return binding.getRoot();
    }
}