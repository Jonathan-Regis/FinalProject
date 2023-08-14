package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This class sets with the recycler and bind data with conversions to be displayed
 */
public class ConversionsRecyclerViewAdapter extends RecyclerView.Adapter<ConversionsRecyclerViewAdapter.MyViewHolder> {
    Context context;

    List<CurrencyConverter> conversionList;

    ConversionsDatabase db;

    CurrencyConverterDAO ccDAO;

    public ConversionsRecyclerViewAdapter(Context context, List<CurrencyConverter> conversionList, ConversionsDatabase db) {
        this.context = context;
        this.conversionList = conversionList;
        this.db = db;
        this.ccDAO = db.ccDAO();
    }

    /**
     * This method creates and returns new instances of an inflater and view
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ConversionsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.conversion_rows, parent, false);
        return new ConversionsRecyclerViewAdapter.MyViewHolder(view);
    }

    /**
     * This method gets the position of the values and what to display
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ConversionsRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.outputAmountDatabase.setText(conversionList.get(position).convertedAmount);
        holder.inputAmountDatabase.setText(conversionList.get(position).beforeAmount);
        holder.outputCurrencyDatabase.setText(conversionList.get(position).convertedCurrency);
        holder.inputCurrencyDatabase.setText(conversionList.get(position).beforeCurrency);

    }

    @Override
    public int getItemCount() {
        return conversionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView inputCurrencyDatabase;
        TextView outputCurrencyDatabase;
        TextView inputAmountDatabase;
        TextView outputAmountDatabase;

        /**
         * binds the ID of values to a database
         * @param itemView creates a view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inputCurrencyDatabase = itemView.findViewById(R.id.beforeCurrencyDB);
            outputCurrencyDatabase = itemView.findViewById(R.id.convertedCurrencyDB);
            inputAmountDatabase = itemView.findViewById(R.id.beforeAmountDB);
            outputAmountDatabase = itemView.findViewById(R.id.convertedAmountDB);


            itemView.setOnClickListener(clk -> {
                deleteConversion();

            });


        }

        /**
         * This method will try to delete a conversion entry and allow you undo a delete for a duration after clicking yes
         */
        public void deleteConversion() {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                CurrencyConverter removedConversion = conversionList.get(position);
                AlertDialog.Builder deleteConversionBuilder = new AlertDialog.Builder(context);
                deleteConversionBuilder.setTitle("Delete this conversion?");
                deleteConversionBuilder.setMessage("Are you sure you want to delete this conversion entry?");
                deleteConversionBuilder.setPositiveButton("Deleted entry", (dialog, which) -> {

                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> {
                        ccDAO.deleteConversion(removedConversion);

                        ((Activity) context).runOnUiThread(() -> {
                            conversionList.remove(position);
                            notifyItemRemoved(position);

                            Snackbar.make(itemView, "Conversion has been deleted", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", v -> {
                                        conversionList.add(position, removedConversion);
                                        notifyItemInserted(position);

                                        Executor threadUndo = Executors.newSingleThreadExecutor();
                                        threadUndo.execute(() -> {
                                            ccDAO.insertConversion(removedConversion);
                                        });
                                    }).show();
                        });
                    });
                });
                deleteConversionBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = deleteConversionBuilder.create();
                alertDialog.show();
            }
        }


    }
}