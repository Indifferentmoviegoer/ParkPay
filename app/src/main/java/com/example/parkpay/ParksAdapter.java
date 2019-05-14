package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;

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

import java.util.List;
import java.util.Objects;

public class ParksAdapter extends RecyclerView.Adapter<ParksAdapter.ParkViewHolder>{

    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PARK_ID ="parkID";
    public static final String APP_PREFERENCES_LAT ="lat";
    private static final String APP_PREFERENCES_LNG ="lng";
    private static final String APP_PREFERENCES_NAME_OBJECT ="nameObject";
    private static final String TAG = "myLogs";

    private SharedPreferences settings;

    private final List<Park> parks;
    private final Context context;

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

        parkViewHolder.cv.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_PARK_ID,parks.get(i).parkId);
            editor.apply();

            ((MainActivity) Objects.requireNonNull(context))
                    .replaceFragments(AttractionsFragment.class);
        });

        parkViewHolder.more.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_PARK_ID,parks.get(i).parkId);
            editor.apply();

            ((MainActivity) Objects.requireNonNull(context))
                    .replaceFragments(AttractionsFragment.class);
        });

        parkViewHolder.parkId.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_NAME_OBJECT,parks.get(i).name);
            editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
            editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
            editor.apply();

            ((MainActivity) Objects.requireNonNull(context))
                    .replaceFragments(MapFragment.class);

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);
        });

        parkViewHolder.map.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_NAME_OBJECT,parks.get(i).name);
            editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
            editor.putFloat(APP_PREFERENCES_LNG,parks.get(i).lngCenter);
            editor.apply();

            ((MainActivity) Objects.requireNonNull(context))
                    .replaceFragments(MapFragment.class);
        });

    }

    @Override
    public int getItemCount() {
        return parks.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ParkViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView parkName;
        final TextView map;
        final TextView more;
        final ImageView parkPhoto;
        final ImageView parkId;

        ParkViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvParks);
            parkName = itemView.findViewById(R.id.park_name);
            map = itemView.findViewById(R.id.map);
            parkPhoto = itemView.findViewById(R.id.park_photo);
            parkId = itemView.findViewById(R.id.park_id);
            more = itemView.findViewById(R.id.more);
        }

    }

}