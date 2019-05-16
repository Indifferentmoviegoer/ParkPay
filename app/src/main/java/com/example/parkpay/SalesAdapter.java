package com.example.parkpay;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SaleViewHolder>{

    private final List<Sale> sales;
    private final Context context;

    SalesAdapter(Context context, List<Sale> persons){
        this.sales = persons;
        this.context = context;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_item, viewGroup, false);
        return new SaleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder saleViewHolder, int i) {
        saleViewHolder.saleName.setText(sales.get(i).name);
        saleViewHolder.personAge.setText(sales.get(i).text);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMMM");


        try {

            Date dateStart = formatter.parse(sales.get(i).dateStart);
            Date dateEnd = formatter.parse(sales.get(i).dateEnd);
            //Date timeValues = formatter.parse(SubItem.get(i));

            saleViewHolder.dateStart.setText(formatterOut.format(dateStart));
            saleViewHolder.dateEnd.setText(formatterOut.format(dateEnd));

            //time.setText(timeValue.format(timeValues));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Glide.with(context).load(sales.get(i).getImageUrl()).into(saleViewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class SaleViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView saleName;
        final TextView personAge;
        final TextView dateStart;
        final TextView dateEnd;
        final ImageView photo;

        SaleViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            saleName = itemView.findViewById(R.id.sale_name);
            personAge = itemView.findViewById(R.id.sale_text);
            dateStart = itemView.findViewById(R.id.dateStart);
            dateEnd = itemView.findViewById(R.id.dateEnd);
            photo = itemView.findViewById(R.id.sale_photo);

        }

    }

}