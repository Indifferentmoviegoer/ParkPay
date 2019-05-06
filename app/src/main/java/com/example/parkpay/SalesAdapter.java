package com.example.parkpay;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SaleViewHolder>{

    List<Sale> sales;
    private Context context;

    SalesAdapter(Context context, List<Sale> persons){
        this.sales = persons;
        this.context = context;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sales_item, viewGroup, false);
        SaleViewHolder pvh = new SaleViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder saleViewHolder, int i) {
        saleViewHolder.saleName.setText(sales.get(i).name);
        saleViewHolder.personAge.setText(sales.get(i).text);
        saleViewHolder.dateStart.setText(sales.get(i).dateStart);
        saleViewHolder.dateEnd.setText(sales.get(i).dateEnd);

        Glide.with(context).load(sales.get(i).getImageUrl()).into(saleViewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class SaleViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView saleName;
        TextView personAge;
        TextView dateStart;
        TextView dateEnd;
        ImageView photo;

        SaleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            saleName = (TextView)itemView.findViewById(R.id.sale_name);
            personAge = (TextView)itemView.findViewById(R.id.sale_text);
            dateStart = (TextView)itemView.findViewById(R.id.dateStart);
            dateEnd = (TextView)itemView.findViewById(R.id.dateEnd);
            photo = (ImageView)itemView.findViewById(R.id.sale_photo);

        }

    }

}