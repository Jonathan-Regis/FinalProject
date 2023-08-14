package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.databinding.ActivityConverterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;



public class ConverterActivity extends AppCompatActivity {


    protected RequestQueue rQueue = null;
    CurrencyConverterDAO ccDAO;
    ActivityConverterBinding binding;
    SharedPreferences prefs;
    EditText beforeAmount;

    EditText beforeCurrency;

    EditText convertedCurrency;


    /**
     * Loads up the currency converter when the app is ran
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConversionsDatabase db = Room.databaseBuilder(getApplicationContext(), ConversionsDatabase.class, "Currency Conversions").build();
        ccDAO = db.ccDAO();

        binding = ActivityConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        beforeAmount = findViewById(R.id.beforeAmount);
        beforeCurrency = findViewById(R.id.beforeCurrency);
        convertedCurrency = findViewById(R.id.convertedCurrency);

        String savedBeforeCurrency = prefs.getString("beforeCurrency", "");//saves previous written data loaded with a key
        String savedBeforeAmount = prefs.getString("beforeAmount", "");//saves previous written data loaded with a key
        String savedConvertedCurrency = prefs.getString("convertedCurrency", "");//saves previous written data loaded with a key

        beforeCurrency.setText(savedBeforeCurrency);//sets previous written data
        beforeAmount.setText(savedBeforeAmount);//sets previous written data
        convertedCurrency.setText(savedConvertedCurrency);//sets previous written data

        rQueue = Volley.newRequestQueue(this);


        /**
         * Checks for any impurities in the edit Texts and displays Toast messages on the result
         */
        binding.convertButton.setOnClickListener(clk -> {
            String inputBeforeAmount = beforeAmount.getText().toString();
            String inputBeforeCurrency = beforeCurrency.getText().toString().toUpperCase();
            String inputConvertedCurrency = convertedCurrency.getText().toString().toUpperCase();
            saveConversionData(inputBeforeAmount, inputBeforeCurrency, inputConvertedCurrency);
            if (inputBeforeAmount.isEmpty()) {
                Toast.makeText(this, "Please enter the amount of money to convert", Toast.LENGTH_LONG).show();
                return;
            } else if (inputBeforeCurrency.isEmpty() || inputBeforeCurrency.length() != 3) {
                Toast.makeText(this, "Please enter a 3 letter currency to convert from", Toast.LENGTH_SHORT).show();
                return;
            } else if (inputConvertedCurrency.isEmpty() || inputConvertedCurrency.length() != 3) {
                Toast.makeText(this, "Please enter a 3 letter currency to convert to", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Successfully Converted", Toast.LENGTH_LONG).show();
            String beforeAmount = binding.beforeAmount.getText().toString();
            String beforeCurrency = binding.beforeCurrency.getText().toString().toUpperCase();
            String convertedCurrency = binding.convertedCurrency.getText().toString().toUpperCase();

            //Strings the complete URL together for the conversions
            String URL = "https://api.getgeoapi.com/v2/currency/convert?format=json&from="
                    + URLEncoder.encode(beforeCurrency)
                    + "&to="
                    + URLEncoder.encode(convertedCurrency)
                    + "&amount="
                    + URLEncoder.encode(beforeAmount)
                    + "&api_key=b3d39bafe1ea0783c3cd6d969913b632a390a463&format=json";


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                    (response) -> {
                        try {
                            JSONObject convRate = response.getJSONObject("rates");
                            JSONObject currency = convRate.getJSONObject(convertedCurrency);
                            String convRateAmount = currency.getString("rate_for_amount");
                            binding.convertedAmount.setText(convRateAmount);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);


                        }
                    },
                    (error) -> {
                    });
            rQueue.add(request);
        });
    }


    /**
     *  Saves the data of the conversion
     * @param beforeCurrency represents old currency
     * @param convertedCurrency represents new conversion currency
     * @param beforeAmount represents the amount to convert
     */
    private void saveConversionData(String beforeCurrency, String convertedCurrency, String beforeAmount) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("beforeCurrency", beforeCurrency);
        editor.putString("convertedCurrency", convertedCurrency);
        editor.putString("beforeAmount", beforeAmount);
        editor.apply();
    }

    /**
     * Shows the menu
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.converter_menu, menu);
        return true;
    }

    private void saveCurrencyConversionToDatabase() {

        String beforeAmount = binding.beforeAmount.getText().toString();
        String convertedAmount = binding.convertedAmount.getText().toString();
        String beforeCurrency = binding.beforeCurrency.getText().toString().toUpperCase();
        String convertedCurrency = binding.convertedCurrency.getText().toString().toUpperCase();

        if("Amount".equals(convertedAmount)){
            Toast.makeText(this, "Cannot save unconverted currencies", Toast.LENGTH_SHORT).show();
            return;
        } else {
            CurrencyConverter conversion = new CurrencyConverter();
            conversion.beforeAmount = beforeAmount;
            conversion.convertedAmount = convertedAmount;
            conversion.beforeCurrency = beforeCurrency;
            conversion.convertedCurrency = convertedCurrency;

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                ccDAO.insertConversion(conversion);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Currency conversion is saved to the database", Toast.LENGTH_LONG).show();
                });
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.box1) {
            saveCurrencyConversionToDatabase();

        } else if (item.getItemId() == R.id.box2) {
            Intent conversionRecycler = new Intent(this, ConversionRecycler.class);
            startActivity(conversionRecycler);
            updateAllConversions();

        } else if (item.getItemId() == R.id.box3) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.instructions));
            alertDialogBuilder.setMessage(getString(R.string.instruction_text));

            alertDialogBuilder.setPositiveButton("I understand", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
        return true;
    }

    /**
     * Updates a list of the conversions using an external API with a url and
     */
    private void updateAllConversions() {
        Thread thread = new Thread(() -> {
            List<CurrencyConverter> conversionList = ccDAO.getAllConversions();

            String apiKey = "b3d39bafe1ea0783c3cd6d969913b632a390a463&format=json";
            String baseUrl = "https://api.getgeoapi.com/v2/currency/convert";

            for (CurrencyConverter conversion : conversionList) {
                String fromCurrency = URLEncoder.encode(conversion.getBeforeCurrency());
                String toCurrency = URLEncoder.encode(conversion.getConvertedCurrency());
                String amount = URLEncoder.encode(conversion.getBeforeAmount());

                //uses this URL for the external API
                String URL = baseUrl
                        + "?format=json&from="
                        + fromCurrency
                        + "&to="
                        + toCurrency
                        + "&amount="
                        + amount +
                        "&api_key=" + apiKey;

                //Creates a request for the conversion data
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                        response -> {
                            try {
                                //returnes the rates and amount
                                JSONObject rates = response.getJSONObject("rates");
                                JSONObject currency = rates.getJSONObject(conversion.getConvertedCurrency());
                                String ratesAmount = currency.getString("rate_for_amount");

                                runOnUiThread(() -> {
                                    binding.convertedAmount.setText(ratesAmount);
                                });

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> {
                        });
                rQueue.add(request);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Database has been updated", Toast.LENGTH_SHORT).show();
            });
        });
        thread.start();
    }
}
