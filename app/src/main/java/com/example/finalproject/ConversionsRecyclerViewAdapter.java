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
 * RecyclerView adapter for displaying currency conversion records in a list.
 */
public class ConversionsRecyclerViewAdapter extends RecyclerView.Adapter<ConversionsRecyclerViewAdapter.MyViewHolder> {
    /**
     * Context of the adapter.
     */
    Context context;
    /**
     * List of currency converter objects.
     */
    List<CurrencyConverter> converterList;
    /**
     * Database instance.
     */
    ConversionsDatabase db;
    /**
     * CurrencyConverterDAO instance.
     */
    CurrencyConverterDAO cDAO;

    /**
     * Constructs an instance of the ConversionsRecyclerViewAdapter.
     *
     * @param context       The context of the adapter.
     * @param converterList The list of currency converter objects.
     * @param db            The database instance.
     */
    public ConversionsRecyclerViewAdapter(Context context, List<CurrencyConverter> converterList, ConversionsDatabase db) {
        this.context = context;
        this.converterList = converterList;
        this.db = db;
        this.cDAO = db.ccDAO();
    }

    @NonNull
    @Override
    public ConversionsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.conversion_rows, parent, false);
        return new ConversionsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionsRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.outputAmountDatabase.setText(converterList.get(position).outputAmount);
        holder.inputAmountDatabase.setText(converterList.get(position).inputAmount);
        holder.outputCurrencyDatabase.setText(converterList.get(position).outputCurrency);
        holder.inputCurrencyDatabase.setText(converterList.get(position).inputCurrency);

    }

    @Override
    public int getItemCount() {
        return converterList.size();
    }

    /**
     * ViewHolder class for managing individual items in the RecyclerView.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView inputCurrencyDatabase;
        TextView outputCurrencyDatabase;
        TextView inputAmountDatabase;
        TextView outputAmountDatabase;
        Button timeExecButton;

        /**
         * Constructs a ViewHolder for managing individual items in the RecyclerView.
         *
         * @param itemView The view of the individual item.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inputCurrencyDatabase = itemView.findViewById(R.id.inputCurrencyDatabase);
            outputCurrencyDatabase = itemView.findViewById(R.id.outputCurrencyDatabase);
            inputAmountDatabase = itemView.findViewById(R.id.inputAmountDatabase);
            outputAmountDatabase = itemView.findViewById(R.id.outputAmountDatabase);
            timeExecButton = itemView.findViewById(R.id.timeExecButton);


            itemView.setOnClickListener(clk -> {
                deleteConversion();

            });

            timeExecButton.setOnClickListener(clk -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {

                    CurrencyConverter cConverter = converterList.get(position);
                    FragmentManager fMgr = ((AppCompatActivity) context).getSupportFragmentManager();
                    ConversionTimeFragment frag = new ConversionTimeFragment(cConverter);


                    FragmentTransaction tx = fMgr.beginTransaction();
                    tx.replace(R.id.fragmentLocation, frag);
                    tx.commit();

                }

            });

        }
        /**
         * Deletes a conversion record from the database and updates the RecyclerView.
         */
        public void deleteConversion() {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                CurrencyConverter removedConversion = converterList.get(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Delete Entry");
                alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
                alertDialogBuilder.setPositiveButton("Delete", (dialog, which) -> {

                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> {
                        cDAO.deleteConversion(removedConversion);

                        ((Activity) context).runOnUiThread(() -> {
                            converterList.remove(position);
                            notifyItemRemoved(position);

                            Snackbar.make(itemView, "Conversion deleted", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", v -> {
                                        converterList.add(position, removedConversion);
                                        notifyItemInserted(position);

                                        Executor threadUndo = Executors.newSingleThreadExecutor();
                                        threadUndo.execute(() -> {
                                            cDAO.insertConversion(removedConversion);
                                        });
                                    }).show();
                        });
                    });
                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }


    }
}