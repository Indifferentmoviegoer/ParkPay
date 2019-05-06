package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Objects;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder>{

    List<Attraction> attractions;
    private Context context;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";
    public static final String APP_PREFERENCES_LAT ="lat";
    public static final String APP_PREFERENCES_LNG ="lng";
    public static final String APP_PREFERENCES_NAME_OBJECT ="nameObject";
    private static final String TAG = "myLogs";

    SharedPreferences settings;

    AttractionsAdapter(Context context, List<Attraction> name){
        this.attractions = name;
        this.context = context;
    }


    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attraction_item, viewGroup, false);
        AttractionViewHolder pvh = new AttractionViewHolder(v);

        settings= Objects.requireNonNull(context)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder attractionViewHolder, int i) {
        attractionViewHolder.attrName.setText(attractions.get(i).name);
        attractionViewHolder.price.setText(attractions.get(i).price);
        attractionViewHolder.bonus.setText(attractions.get(i).bonus);
        attractionViewHolder.text.setText(attractions.get(i).text);
        attractionViewHolder.weight.setText(attractions.get(i).weight);
        attractionViewHolder.growth.setText(attractions.get(i).growth);
        attractionViewHolder.ageMin.setText(attractions.get(i).ageMin);
        attractionViewHolder.ageMax.setText(attractions.get(i).ageMax);

        Glide.with(context).load(attractions.get(i).getImageUrl()).into(attractionViewHolder.attrPhoto);

        attractionViewHolder.mapAttr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME_OBJECT,attractions.get(i).name);
                editor.putFloat(APP_PREFERENCES_LAT,attractions.get(i).lat);
                editor.putFloat(APP_PREFERENCES_LNG,attractions.get(i).lng);
                editor.apply();

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);

                ((MainActivity) Objects.requireNonNull(context))
                        .replaceFragments(MapFragment.class);
            }
        });

        attractionViewHolder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME_OBJECT,attractions.get(i).name);
                editor.putFloat(APP_PREFERENCES_LAT,attractions.get(i).lat);
                editor.putFloat(APP_PREFERENCES_LNG,attractions.get(i).lng);
                editor.apply();

                ((MainActivity) Objects.requireNonNull(context))
                        .replaceFragments(MapFragment.class);

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView attrName;
        TextView price;
        TextView bonus;
        TextView text;
        TextView weight;
        TextView growth;
        TextView ageMin;
        TextView ageMax;
        TextView mapAttr;
        ImageView attrPhoto;
        ImageView map;

        AttractionViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvAttr);
            attrName = (TextView)itemView.findViewById(R.id.attraction_name);
            price = (TextView)itemView.findViewById(R.id.price);
            bonus = (TextView)itemView.findViewById(R.id.bonus);
            text = (TextView)itemView.findViewById(R.id.text);
            weight = (TextView)itemView.findViewById(R.id.weight);
            growth = (TextView)itemView.findViewById(R.id.growth);
            ageMin = (TextView)itemView.findViewById(R.id.age_min);
            ageMax = (TextView)itemView.findViewById(R.id.age_max);
            mapAttr = (TextView)itemView.findViewById(R.id.mapAttr);
            attrPhoto = (ImageView)itemView.findViewById(R.id.attraction_photo);
            map = (ImageView)itemView.findViewById(R.id.attraction_id);
        }

    }

}