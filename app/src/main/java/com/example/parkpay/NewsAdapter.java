package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Objects;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private final List<News> news;
    private final Context context;

    NewsAdapter(Context context, List<News> news){
        this.news = news;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news, viewGroup, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        newsViewHolder.newsName.setText(news.get(i).name);



        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMMM");


        try {

            Date date = formatter.parse(news.get(i).date);
            //Date timeValues = formatter.parse(SubItem.get(i));

            newsViewHolder.dateNews.setText(formatterOut.format(Objects.requireNonNull(date)));

            //time.setText(timeValue.format(timeValues));

        } catch (ParseException e) {
            e.printStackTrace();
        }



        newsViewHolder.text.setText(news.get(i).text);
        newsViewHolder.link.setText(news.get(i).link);

        Glide.with(context).load(news.get(i).getImageUrl()).into(newsViewHolder.newsPhoto);

        newsViewHolder.link.setOnClickListener(view -> {
            Uri adress= Uri.parse(news.get(i).link);
            Intent browser= new Intent(Intent.ACTION_VIEW, adress);
            context.startActivity(browser);
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        final TextView newsName;
        final ImageView newsPhoto;
        final TextView dateNews;
        final TextView text;
        final TextView link;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsName = itemView.findViewById(R.id.news_name);
            newsPhoto = itemView.findViewById(R.id.news_photo);
            dateNews = itemView.findViewById(R.id.dateNews);
            text = itemView.findViewById(R.id.news_text);
            link = itemView.findViewById(R.id.news_link);
        }

    }

}