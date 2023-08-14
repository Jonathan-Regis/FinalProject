package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;

import com.example.finalproject.databinding.ActivityConversionRecyclerBinding;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This class displays a recycler of all the conversions
 */
public class ConversionRecycler extends AppCompatActivity {

    ActivityConversionRecyclerBinding binding;
    CurrencyConverterDAO ccDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConversionRecyclerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ConversionsDatabase db = Room.databaseBuilder(getApplicationContext(), ConversionsDatabase.class, "Currency Conversions").build();
        ccDAO = db.ccDAO();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            List<CurrencyConverter> conversionList = ccDAO.getAllConversions();


            runOnUiThread(()->{
                ConversionsRecyclerViewAdapter adapter = new ConversionsRecyclerViewAdapter(this, conversionList, db);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            });
        });




    }

}
