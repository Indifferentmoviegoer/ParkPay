package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class ExpListAdapter extends BaseExpandableListAdapter{

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_DETAIL_CARD ="detailCard";
    SharedPreferences settings;

    private ArrayList<ArrayList<String>> mGroups;
    private ArrayList<String> child;
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
//        if (convertView == null) {
//            if(isLastChild){
//                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.add_child_view, null);
//            }
//            if(!isLastChild){
//                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflater.inflate(R.layout.child_view, null);
//            }
//        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.child_view, null);

        TextView cardChild = (TextView) convertView.findViewById(R.id.cardChild);
        cardChild.setText(mGroups.get(groupPosition).get(childPosition));

        Button button = (Button)convertView.findViewById(R.id.buttonChild);
        if(isLastChild){
            button.setText("Добавить");
            button.getBackground().setColorFilter(Color.parseColor("#71CC32"), PorterDuff.Mode.MULTIPLY);
        }
        if(!isLastChild){
            button.setText("Подробнее");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLastChild) {

                    if(groupPosition==0) {
                        Intent i = new Intent(mContext, AddCardActivity.class);
                        mContext.startActivity(i);
                    }

                    if(groupPosition==1) {
                        ((MainActivity) Objects.requireNonNull(mContext))
                                .replaceFragments(AddVirtualCardFragment.class);
//                        Intent i = new Intent(mContext, AddCardActivity.class);
//                        i.putExtra("POSITION_GROUP", 1);
//                        mContext.startActivity(i);
                    }
                }

                if(!isLastChild) {

                    if(groupPosition==0) {

                        if (settings.contains(APP_PREFERENCES_CARDS)) {
                            child= MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                        }
                        Intent i = new Intent(mContext, DetailCardActivity.class);
                        i.putExtra("POSITION_CARD", child.get(childPosition));
                        i.putExtra("POSITION_GROUP", 0);
                        mContext.startActivity(i);
                    }

                    if(groupPosition==1) {

                        if (settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)) {
                            child = MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                        }
                        Intent i = new Intent(mContext, DetailCardActivity.class);
                        i.putExtra("POSITION_CARD", child.get(childPosition));
                        i.putExtra("POSITION_GROUP", 1);
                        mContext.startActivity(i);
                    }
                }
            }
        });
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
