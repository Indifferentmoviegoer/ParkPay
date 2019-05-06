package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    List<News> news;
    private Context context;

    NewsAdapter(Context context, List<News> news){
        this.news = news;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        NewsViewHolder pvh = new NewsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        newsViewHolder.newsName.setText(news.get(i).name);
        newsViewHolder.dateNews.setText(news.get(i).date);
        newsViewHolder.text.setText(news.get(i).text);
        newsViewHolder.link.setText(news.get(i).link);

        Glide.with(context).load(news.get(i).getImageUrl()).into(newsViewHolder.newsPhoto);

        newsViewHolder.link.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                Uri adress= Uri.parse(news.get(i).link);
                Intent browser= new Intent(Intent.ACTION_VIEW, adress);
                context.startActivity(browser);
            }

        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView newsName;
        ImageView newsPhoto;
        TextView dateNews;
        TextView text;
        TextView link;

        NewsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvNews);
            newsName = (TextView)itemView.findViewById(R.id.news_name);
            newsPhoto = (ImageView)itemView.findViewById(R.id.news_photo);
            dateNews = (TextView)itemView.findViewById(R.id.dateNews);
            text = (TextView)itemView.findViewById(R.id.news_text);
            link = (TextView)itemView.findViewById(R.id.news_link);
        }

    }

}