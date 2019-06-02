package com.example.parkpay;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionsViewHolder>{

    private final List<Transaction> transactions;
    private final Context context;

    TransactionAdapter(Context context, List<Transaction> transactions){
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transaction, viewGroup, false);
        return new TransactionsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder transactionsViewHolder, int i) {
        transactionsViewHolder.nameTransaction.setText(transactions.get(i).name);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMM");
        SimpleDateFormat timeValue = new SimpleDateFormat("HH:mm:ss");


        try {



            Date date = formatter.parse(transactions.get(i).date);
            Date timeValues = formatter.parse(transactions.get(i).date);

//            if(date.isEqual(historyDate) && todayDate.before(futureDate)) {
//                // In between
//            }

            transactionsViewHolder.dateTransaction.setText(formatterOut.format(Objects.requireNonNull(date)));

            transactionsViewHolder.timeTransaction.setText(timeValue.format(Objects.requireNonNull(timeValues)));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        String plus="+"+transactions.get(i).value;
        transactionsViewHolder.valueTransaction.setText(plus);
        transactionsViewHolder.valueTransaction.setTextColor(Color.parseColor("#8BC34A"));

        Glide.with(context).load(transactions.get(i).photo).into(transactionsViewHolder.imTransaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class TransactionsViewHolder extends RecyclerView.ViewHolder {

        final TextView nameTransaction;
        final TextView dateTransaction;
        final TextView timeTransaction;
        final TextView valueTransaction;
        final ImageView imTransaction;

        TransactionsViewHolder(View itemView) {
            super(itemView);

            nameTransaction = itemView.findViewById(R.id.nameTransaction);
            dateTransaction = itemView.findViewById(R.id.dateTransaction);
            timeTransaction = itemView.findViewById(R.id.timeTransaction);
            valueTransaction = itemView.findViewById(R.id.valueTransaction);
            imTransaction = itemView.findViewById(R.id.imTransaction);
        }

    }

}