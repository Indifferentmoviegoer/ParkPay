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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder>{

    private final List<Attraction> attractions;
    private final Context context;

    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_LAT ="lat";
    private static final String APP_PREFERENCES_LNG ="lng";
    private static final String APP_PREFERENCES_NAME_OBJECT ="nameObject";
    private static final String TAG = "myLogs";

    private SharedPreferences settings;

    AttractionsAdapter(Context context, List<Attraction> name){
        this.attractions = name;
        this.context = context;
    }


    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_attraction, viewGroup, false);
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

        if(attractions.get(i).text.contains("null")){
            attractionViewHolder.text.setText("");
        }
        else {
            attractionViewHolder.text.setText(attractions.get(i).text);
        }


        String weight=attractions.get(i).weight+" кг";
        String growth=attractions.get(i).growth+" см";
        String ageMin=attractions.get(i).ageMin+"+ лет";


        if(attractions.get(i).ageMin.contains("null"))
        {
            ageMin="4+ лет";
        }
        if(attractions.get(i).weight.contains("null")){

            weight="100 кг";
        }
        if(attractions.get(i).growth.contains("null")){

            growth="130 см";
        }

        attractionViewHolder.weight.setText(weight);
        attractionViewHolder.growth.setText(growth);
        attractionViewHolder.ageMin.setText(ageMin);

        String levelFear=attractions.get(i).levelFear;



        if(levelFear.contains("низкий")){

            Glide.with(context).load(R.drawable.ic_baby).into(attractionViewHolder.levelFear);
        }

        else if(levelFear.contains("средний")){

            Glide.with(context).load(R.drawable.ic_family).into(attractionViewHolder.levelFear);
        }

        else if(levelFear.contains("высокий")){

            Glide.with(context).load(R.drawable.ic_adult).into(attractionViewHolder.levelFear);
        }
        else{
            Glide.with(context).load(R.drawable.ic_family).into(attractionViewHolder.levelFear);
        }
//        attractionViewHolder.ageMax.setText(attractions.get(i).ageMax);




        if(attractions.get(i).getImageUrl().contains("null")){

            Glide.with(context).load(R.drawable.placeholder).into(attractionViewHolder.attrPhoto);
        }
        else {
            Glide.with(context).load(attractions.get(i).getImageUrl()).into(attractionViewHolder.attrPhoto);
        }


        attractionViewHolder.mapAttr.setOnClickListener(v -> {

            if(attractions.get(i).lat==0.0||attractions.get(i).lng==0.0) {

                Toast.makeText(context, "Координаты не указаны!", Toast.LENGTH_SHORT).show();



            }
            else {
                boolean checkConnection=MainActivity.isOnline(context);

                if(checkConnection) {

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(APP_PREFERENCES_NAME_OBJECT, attractions.get(i).name);
                    editor.putFloat(APP_PREFERENCES_LAT, attractions.get(i).lat);
                    editor.putFloat(APP_PREFERENCES_LNG, attractions.get(i).lng);
                    editor.apply();

                    ((MainActivity) Objects.requireNonNull(context))
                            .replaceFragments(MapFragment.class);

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);
                }
                else {

                    Toast.makeText(context, "Отсутствует интернет соединение!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        attractionViewHolder.map.setOnClickListener(v -> {
            if(attractions.get(i).lat==0.0||attractions.get(i).lng==0.0) {

                Toast.makeText(context, "Координаты не указаны!", Toast.LENGTH_SHORT).show();



            }
            else {
                boolean checkConnection=MainActivity.isOnline(context);

                if(checkConnection) {

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(APP_PREFERENCES_NAME_OBJECT, attractions.get(i).name);
                    editor.putFloat(APP_PREFERENCES_LAT, attractions.get(i).lat);
                    editor.putFloat(APP_PREFERENCES_LNG, attractions.get(i).lng);
                    editor.apply();

                    ((MainActivity) Objects.requireNonNull(context))
                            .replaceFragments(MapFragment.class);

//                Intent intent = new Intent(context, Main2Activity.class);
//                context.startActivity(intent);
                }
                else {

                    Toast.makeText(context, "Отсутствует интернет соединение!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {

        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView attrName;
        final TextView price;
        final TextView bonus;
        final TextView text;
        final TextView weight;
        final TextView growth;
        final TextView ageMin;
        //        final TextView ageMax;
        final TextView mapAttr;
        final ImageView attrPhoto;
        final ImageView map;
        final ImageView levelFear;

        AttractionViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvAttr);
            attrName = itemView.findViewById(R.id.attraction_name);
            price = itemView.findViewById(R.id.price);
            bonus = itemView.findViewById(R.id.bonus);
            text = itemView.findViewById(R.id.text);
            weight = itemView.findViewById(R.id.weight);
            growth = itemView.findViewById(R.id.growth);
            ageMin = itemView.findViewById(R.id.age_min);
//            ageMax = itemView.findViewById(R.id.age_max);
            mapAttr = itemView.findViewById(R.id.mapAttr);
            attrPhoto = itemView.findViewById(R.id.attraction_photo);
            map = itemView.findViewById(R.id.attraction_id);
            levelFear = itemView.findViewById(R.id.levelFear);
        }

    }

}