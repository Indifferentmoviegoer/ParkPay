package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;
import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.ColorScheme;
import ru.yandex.money.android.sdk.MockConfiguration;
import ru.yandex.money.android.sdk.PaymentParameters;
import ru.yandex.money.android.sdk.TestParameters;
import ru.yandex.money.android.sdk.TokenizationResult;
import ru.yandex.money.android.sdk.UiParameters;

public class PayActivity extends AppCompatActivity {

    private EditText amountPay;

    private BigDecimal sum;

    private Context c;

    private static final String TAG = "myLogs";

    private static final int REQUEST_CODE_TOKENIZE = 33;

    private SharedPreferences settings;

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    private static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String APP_PREFERENCES_CARD_NAME ="cardName";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MSG ="Message";

    private TokenizationResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        amountPay= findViewById(R.id.amountPay);
        AppCompatButton buttonPay = findViewById(R.id.buttonPay);
        ImageView backPay = findViewById(R.id.backPay);
        AppCompatTextView titlePay= findViewById(R.id.titlePay);
        AppCompatTextView numberPay = findViewById(R.id.numberPay);
        AppCompatImageView imPay = findViewById(R.id.imPay);


        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        Glide.with(c)
                .load(R.drawable.pay)
                .thumbnail(0.5f)
                .dontAnimate()
                .into(imPay);

        String name=settings.getString(APP_PREFERENCES_CARD_NAME,"")+"";
        String number=settings.getString(APP_PREFERENCES_CARD_CODE,"")+"";

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("____.__");
        MaskImpl mask = MaskImpl.createTerminated(slots);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(amountPay);

        titlePay.setText(name);
        numberPay.setText(number);

        backPay.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });

        buttonPay.setOnClickListener(v -> {

            if(amountPay.getText().toString().equals("")||amountPay.getText().length() == 0)
            {
                Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            }

            else {
                sum=new BigDecimal(amountPay.getText().toString());
                timeToStartCheckout();
                //timeToStartCheckoutTest();
//                    Intent intent = new Intent(getApplicationContext(),
//                            MainActivity.class);
//                    startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TOKENIZE) {
            switch (resultCode) {
                case RESULT_OK:
                    // successful tokenization
                    result = Checkout.createTokenizationResult(data);
                    Toast.makeText(getApplicationContext(),"Успешно",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Результат "+result);
                    //doPostRequest("http://192.168.252.199/card/add_money");
                    doPostRequest("https://api.mobile.goldinnfish.com/card/create_payment");

                case RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),"Отмена",Toast.LENGTH_SHORT).show();
//                    doPostRequest("http://192.168.252.199/card/add_money");
                    // user canceled tokenization
                    break;
            }
        }
    }

    private void timeToStartCheckout() {
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Пополнение карты",
                settings.getString(APP_PREFERENCES_CARD_CODE,"")+"",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856"
        );

        UiParameters uiParameters = new UiParameters(false, new ColorScheme(Color.parseColor("#3F51B5")));

        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters,new TestParameters(),uiParameters);
//        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters,new TestParameters(),uiParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }
    void timeToStartCheckoutTest() {
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Пополнение карты",
                settings.getString(APP_PREFERENCES_CARD_CODE,"")+"",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856"
        );
        UiParameters uiParameters = new UiParameters(false, new ColorScheme(Color.parseColor("#3F51B5")));

        TestParameters testParameters = new TestParameters(true, true, new MockConfiguration(false, true, 5));
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters, testParameters, uiParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }

    private void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
//            json.put("token",settings.getString(APP_PREFERENCES_TOKEN,""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE,""));
            json.put("value",sum);
            json.put("token",result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                runOnUiThread(() -> {
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject Jobject = new JSONObject(jsonData);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();

//                        if(settings.contains(APP_PREFERENCES_STATUS)) {
//
//                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

//                                Toast.makeText(c,settings.getString(APP_PREFERENCES_MSG, "")
//                                                ,Toast.LENGTH_SHORT).show();

                        Toast.makeText(c,"Успешно"
                                ,Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(c,
                                        MainActivity.class);
                                startActivity(intent);
//                            }
//                        }

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }

}
