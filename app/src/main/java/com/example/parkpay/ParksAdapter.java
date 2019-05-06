package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
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

public class ParksAdapter extends RecyclerView.Adapter<ParksAdapter.ParkViewHolder>{

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";
    public static final String APP_PREFERENCES_LAT ="lat";
    public static final String APP_PREFERENCES_LNG ="lng";
    public static final String APP_PREFERENCES_NAME_OBJECT ="nameObject";
    private static final String TAG = "myLogs";

    SharedPreferences settings;

    List<Park> parks;
    private Context context;

    ParksAdapter(Context context,List<Park> name){
        this.parks = name;
        this.context = context;
    }


    @NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.park_item, viewGroup, false);
        ParkViewHolder pvh = new ParkViewHolder(v);

        settings= Objects.requireNonNull(context)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder parkViewHolder, int i) {
        parkViewHolder.parkName.setText(parks.get(i).name);

        Glide.with(context).load(parks.get(i).getImageUrl()).into(parkViewHolder.parkPhoto);

        parkViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_PARK_ID,parks.get(i).parkId);
                editor.apply();

                ((MainActivity) Objects.requireNonNull(context))
                        .replaceFragments(AttractionsFragment.class);
            }
        });

        parkViewHolder.parkId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME_OBJECT,parks.get(i).name);
                editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
                editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
                editor.apply();

                ((MainActivity) Objects.requireNonNull(context))
                        .replaceFragments(MapFragment.class);

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);
            }
        });

        parkViewHolder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME_OBJECT,parks.get(i).name);
                editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
                editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
                editor.apply();

                ((MainActivity) Objects.requireNonNull(context))
                        .replaceFragments(MapFragment.class);
            }
        });

    }

    @Override
    public int getItemCount() {
        return parks.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ParkViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView parkName;
        TextView map;
        ImageView parkPhoto;
        ImageView parkId;

        ParkViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cvParks);
            parkName = (TextView)itemView.findViewById(R.id.park_name);
            map = (TextView)itemView.findViewById(R.id.map);
            parkPhoto = (ImageView)itemView.findViewById(R.id.park_photo);
            parkId = (ImageView)itemView.findViewById(R.id.park_id);
        }

    }

}