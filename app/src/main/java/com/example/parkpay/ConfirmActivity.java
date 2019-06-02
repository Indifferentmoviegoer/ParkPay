package com.example.parkpay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import ru.yandex.money.android.sdk.Checkout;

public class ConfirmActivity extends AppCompatActivity {


    private Context c;

    private SharedPreferences settings;

    private static final String APP_PREFERENCES = "mysettings";
    private static final String CONFIRM = "confirm";

    private static final String TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_confirm);

        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        timeToStart3DS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    // 3ds прошел

                    Toast.makeText(c, "Успешно!"
                            , Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(c,
                            MainActivity.class);
                    startActivity(intent);
                    break;
                case RESULT_CANCELED:

                    Toast.makeText(c, "Отмена"
                            , Toast.LENGTH_SHORT).show();

                    Intent cancel = new Intent(c,
                            MainActivity.class);
                    startActivity(cancel);

                    break;
                case Checkout.RESULT_ERROR:

//                    Toast.makeText(c,"Отмеана "+ data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION)+"   "+data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL),Toast.LENGTH_LONG).show();

                    // data.getIntExtra(Checkout.EXTRA_ERROR_CODE);
//                     data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION);
//                     data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL);
                    break;
            }
        }
    }

    private void timeToStart3DS() {

        Log.d(settings.getString(CONFIRM,""),TAG);

        Intent intent = Checkout.create3dsIntent(
                this,
                settings.getString(CONFIRM,"")
        );
        startActivityForResult(intent, 1);
    }

}
