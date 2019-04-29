package com.example.parkpay;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    TextView editProfile;
    TextView name;
    TextView signOut;
    TextView quantityVisits;
    ImageView imageProfile;

    private PublisherAdView adView;

    PublisherAdRequest adRequest;

    Context c;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NOTIFICATION = "TURN_NOTIFICATION";
    public static final String APP_PREFERENCES_NAME = "Name";
    public static final String APP_PREFERENCES_TOKEN = "Token";
    public static final String APP_PREFERENCES_PHOTO = "Photo";
    public static final String APP_PREFERENCES_QUANTITY_VISITS = "quantityVisits";
    SharedPreferences settings;
    Switch notification;

    MapView mapView;
    GoogleMap gmap;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyAkEPoaujC2ZE7bNYANSH4J7XV-UgzVfSY";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        MapKitFactory.setApiKey("444d4c32-53aa-42aa-a7c5-2e2c0caac1ac");
//        MapKitFactory.initialize(Objects.requireNonNull(getContext()));

        View view = inflater.inflate(R.layout.fragment_map, container, false);


        //imageProfile=(ImageView) view.findViewById(R.id.imageProfile);

        c = getContext();

        settings = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        //mapView = (MapView) view.findViewById(R.id.mapView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(16);

        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);

        LatLng ny = new LatLng(45.057965, 38.992034);

        addMarker();

        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

    }

    private void addMarker(){

        if(null != gmap){
            gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(45.058338, 38.991372))
                    .title("Аттракцион 1")
                    .draggable(true)
            );

            gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(45.058189, 38.991636))
                    .title("Аттракцион 2")
                    .draggable(true)
            );

            gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(45.057458, 38.991373))
                    .title("Аттракцион 3")
                    .draggable(true)
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
