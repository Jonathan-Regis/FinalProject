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



public class ConversionRecycler extends AppCompatActivity {

    ActivityConversionRecyclerBinding binding;
    CurrencyConverterDAO cDAO;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConversionRecyclerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ConversionsDatabase db = Room.databaseBuilder(getApplicationContext(), ConversionsDatabase.class, "database-name").build();
        cDAO = db.ccDAO();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            List<CurrencyConverter> converterList = cDAO.getAllConversions();


            runOnUiThread(()->{
                ConversionsRecyclerViewAdapter adapter = new ConversionsRecyclerViewAdapter(this, converterList, db);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            });
        });




    }

}
