package com.example.parkpay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class FragmentProfile extends Fragment {
    TextView editProfile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        editProfile=(TextView)view.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Create new fragment and transaction
//                Fragment newFragment = new FragmentEditProfile();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragments(FragmentEditProfile.class);
            }
        });

        return view;
    }
}
