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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This class represents the main activity of the currency converter app.
 * It allows users to convert currency values and view their conversion history.
 */
public class ConverterActivity extends AppCompatActivity {


    protected RequestQueue queue = null;
    CurrencyConverterDAO cDAO;
    ActivityConverterBinding binding;
    SharedPreferences prefs;
    EditText inputAmount;
    EditText inputCurrency;
    EditText outputCurrency;

    /**
     * Called when the activity is first created.
     * Initializes the UI components, handles button clicks, and performs currency conversion.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        inputAmount = findViewById(R.id.inputAmount);
        inputCurrency = findViewById(R.id.inputCurrency);
        outputCurrency = findViewById(R.id.outputCurrency);
        String savedInputCurrency = prefs.getString("inputCurrency", "");
        String savedOutputCurrency = prefs.getString("outputCurrency", "");
        String savedInputAmount = prefs.getString("inputAmount", "");


        inputAmount.setText(savedInputAmount);
        inputCurrency.setText(savedInputCurrency);
        outputCurrency.setText(savedOutputCurrency);


        queue = Volley.newRequestQueue(this);
        /**This is the Currency Conversion database*/
        ConversionsDatabase db = Room.databaseBuilder(getApplicationContext(), ConversionsDatabase.class, "database-name").build();
        cDAO = db.ccDAO();


        /**Inside this onClickListener is all for the API url and the convert button functions*/
        binding.convertButton.setOnClickListener(clk -> {
            String newInputCurrency = inputCurrency.getText().toString();
            String newInputAmount = inputAmount.getText().toString();
            String newOutputCurrency = outputCurrency.getText().toString();
            saveConversionDataToSharedPreferences(newInputCurrency, newOutputCurrency, newInputAmount);
            if (newInputAmount.isEmpty()) {
                // Handle empty inputAmount, for example, show a toast message
                Toast.makeText(this, "Please enter an amount to convert", Toast.LENGTH_SHORT).show();
                return; // Exit the onClickListener
            } else if (newInputCurrency.isEmpty() || newInputCurrency.length() != 3) {
                Toast.makeText(this, "Please enter a 3 letter currency", Toast.LENGTH_SHORT).show();
                return;
            } else if (newOutputCurrency.isEmpty() || newOutputCurrency.length() != 3) {
                Toast.makeText(this, "Please enter a 3 letter currency", Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(this, "Converted", Toast.LENGTH_LONG).show();
//          String URL = "https://api.getgeoapi.com/v2/currency/convert?format=json&from=CAD&to=USD&amount=10&api_key=c1f37b28035f89328e61c24caefd20d99f97cdf0&format=json";
            String inputCurrency = binding.inputCurrency.getText().toString().toUpperCase();
            String outputCurrency = binding.outputCurrency.getText().toString().toUpperCase();
            String inputAmount = binding.inputAmount.getText().toString();


            String URL = "https://api.getgeoapi.com/v2/currency/convert?format=json&from="
                    + URLEncoder.encode(inputCurrency)
                    + "&to="
                    + URLEncoder.encode(outputCurrency)
                    + "&amount="
                    + URLEncoder.encode(inputAmount)
                    + "&api_key=c1f37b28035f89328e61c24caefd20d99f97cdf0&format=json";


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                    (response) -> {
                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            JSONObject currency = rates.getJSONObject(outputCurrency);
                            String ratesAmount = currency.getString("rate_for_amount");
                            binding.outputAmount.setText(ratesAmount);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);


                        }
                    },
                    (error) -> {
                        int test = 0;
                    });
            queue.add(request);
        });
    }
    /**
     * Returns the current date and time in a formatted string.
     *
     * @return A formatted string representing the current date and time.
     */
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy \nhh:mm:ss a");
        return sdf.format(new Date());
    }
    /**
     * Creates the options menu in the app's toolbar.
     *
     * @param menu The menu to be populated.
     * @return True if the menu was successfully created, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu_converter, menu);
        return true;
    }
    /**
     * Handles the selection of items from the options menu.
     *
     * @param item The selected menu item.
     * @return True if the menu item selection was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            saveCurrencyConversionToDatabase();
        } else if (item.getItemId() == R.id.item2) {
            Intent conversionRecycler = new Intent(this, ConversionRecycler.class);
            startActivity(conversionRecycler);
            updateAllConversions();
        } else if (item.getItemId() == R.id.item3) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.instructions));
            alertDialogBuilder.setMessage(getString(R.string.instruction_text));

            alertDialogBuilder.setPositiveButton("Got It", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
        return true;
    }
    /**
     * Saves a currency conversion record to the database.
     */
    private void saveCurrencyConversionToDatabase() {

        String inputAmount = binding.inputAmount.getText().toString();
        String outputAmount = binding.outputAmount.getText().toString();
        String inputCurrency = binding.inputCurrency.getText().toString().toUpperCase();
        String outputCurrency = binding.outputCurrency.getText().toString().toUpperCase();
        if("Amount".equals(outputAmount)){
            Toast.makeText(this, "Cannot save unconverted currencies", Toast.LENGTH_SHORT).show();
            return;
        } else {


            CurrencyConverter conversion = new CurrencyConverter();
            conversion.inputAmount = inputAmount;
            conversion.outputAmount = outputAmount;
            conversion.inputCurrency = inputCurrency;
            conversion.outputCurrency = outputCurrency;
            conversion.timeExecuted = getCurrentTime();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                cDAO.insertConversion(conversion);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Currency conversion is saved to the database", Toast.LENGTH_LONG).show();
                });
            }); }
    }
    /**
     * Saves conversion data to SharedPreferences for future use.
     *
     * @param inputCurrency  The input currency code.
     * @param outputCurrency The output currency code.
     * @param inputAmount    The input amount.
     */
    private void saveConversionDataToSharedPreferences(String inputCurrency, String outputCurrency, String inputAmount) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("inputCurrency", inputCurrency);
        editor.putString("outputCurrency", outputCurrency);
        editor.putString("inputAmount", inputAmount);
        editor.apply();
    }
    /**
     * Updates all currency conversions from the database using a separate thread.
     */
    private void updateAllConversions() {
        Thread thread = new Thread(() -> {
            List<CurrencyConverter> conversionList = cDAO.getAllConversions();

            String apiKey = "c1f37b28035f89328e61c24caefd20d99f97cdf0";
            String baseUrl = "https://api.getgeoapi.com/v2/currency/convert";

            for (CurrencyConverter conversion : conversionList) {
                String fromCurrency = URLEncoder.encode(conversion.getInputCurrency());
                String toCurrency = URLEncoder.encode(conversion.getOutputCurrency());
                String amount = URLEncoder.encode(conversion.getInputAmount());

                String URL = baseUrl + "?format=json&from=" + fromCurrency + "&to=" + toCurrency +
                        "&amount=" + amount + "&api_key=" + apiKey;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                        response -> {
                            try {
                                JSONObject rates = response.getJSONObject("rates");
                                JSONObject currency = rates.getJSONObject(conversion.getOutputCurrency());
                                String ratesAmount = currency.getString("rate_for_amount");

                                // Update UI on the main thread
                                runOnUiThread(() -> {
                                    binding.outputAmount.setText(ratesAmount);
                                });

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> {
                            // Handle error if needed
                        });

                queue.add(request);
            }

            // Inform the user that the database has been updated
            runOnUiThread(() -> {
                Toast.makeText(this, "Database has been updated", Toast.LENGTH_LONG).show();
            });
        });

        thread.start();
    }



}
