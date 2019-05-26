package com.example.parkpay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

//public class CameraFragment extends Fragment implements OnBackPressedListener {
public class CameraFragment extends Fragment {

    private Context c;
    private static final String TAG = "myLogs";
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_CARD ="Card";
    private SharedPreferences settings;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera,container,false);

        c=getContext();
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        IntentIntegrator
                .forSupportFragment(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Сканирование QR кода")
                .initiateScan();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(result != null) {
            if(result.getContents() == null) {

                Log.d(TAG,"Отменено");

                Intent i = new Intent(c,
                        MainActivity.class);
                startActivity(i);
                Objects.requireNonNull(getActivity()).finish();

            } else {

                Log.d(TAG,"Результат: " + result.getContents());

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_CARD,result.getContents());
                editor.apply();
                Intent i = new Intent(c,
                        AddCardActivity.class);
                startActivity(i);
                Objects.requireNonNull(getActivity()).finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }



//    @Override
//    public void onBackPressed() {
//
//    }

}
