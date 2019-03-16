package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Objects;

import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.PaymentParameters;

public class ExpListAdapter extends BaseExpandableListAdapter{

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    SharedPreferences settings;

    private ArrayList<ArrayList<String>> mGroups;
    ArrayList<String> child;
    private Context mContext;

    public ExpListAdapter (Context context,ArrayList<ArrayList<String>> groups){
        mContext = context;
        mGroups = groups;
        child =new ArrayList<String>();
        settings= Objects.requireNonNull(mContext)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        //textGroup.setText("Group " + Integer.toString(groupPosition));
        if (groupPosition==0){
            textGroup.setText("Реальные карты");
        }
        if (groupPosition==1){
            textGroup.setText("Виртуальные карты");
        }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            if(isLastChild){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.add_child_view, null);
            }
            if(!isLastChild){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_view, null);
            }

        }
        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(mGroups.get(groupPosition).get(childPosition));

        Button button = (Button)convertView.findViewById(R.id.buttonChild);
        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        if(isLastChild) {
            delete.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLastChild) {
                    if(groupPosition==0) {
                        ArrayList<String> children1 = new ArrayList<String>();
                        if (settings.contains(APP_PREFERENCES_CARDS)) {
                            children1 = getArrayList(APP_PREFERENCES_CARDS);
                        }
                        children1.add("1212121");
                        saveArrayList(children1, APP_PREFERENCES_CARDS);
                        notifyDataSetChanged();
                    }
                    if(groupPosition==1) {
                        ArrayList<String> children2 = new ArrayList<String>();
                        if (settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)) {
                            children2 = getArrayList(APP_PREFERENCES_VIRTUAL_CARDS);
                        }
                        children2.add("1212121");
                        notifyDataSetChanged();
                        saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS);
                    }
                }
                if(!isLastChild) {
                    Intent intent = new Intent(mContext,
                            PayActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLastChild) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Вы действительно хотите удалить данную карту?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //ArrayList<String> child =new ArrayList<String>();
                                            //mGroups.get(groupPosition);

                                    if(groupPosition==0) {
                                        if (settings.contains(APP_PREFERENCES_CARDS)) {
                                            child = getArrayList(APP_PREFERENCES_CARDS);

                                        }
                                        child.remove(childPosition);
                                        notifyDataSetChanged();
                                        saveArrayList(child, APP_PREFERENCES_CARDS);
                                    }

                                    if(groupPosition==1) {
                                        if (settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)) {
                                            child = getArrayList(APP_PREFERENCES_VIRTUAL_CARDS);
                                        }
                                        child.remove(childPosition);
                                        notifyDataSetChanged();
                                        saveArrayList(child, APP_PREFERENCES_VIRTUAL_CARDS);
                                    }
                                    //ArrayList<String> child =mGroups.get(groupPosition);
                                    //child.remove(childPosition);
                                    //notifyDataSetChanged();
                                }
                            });
                    builder.setNegativeButton("Нет",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key){
        Gson gson = new Gson();
        String json = settings.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
