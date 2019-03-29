package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.MockConfiguration;
import ru.yandex.money.android.sdk.PaymentParameters;
import ru.yandex.money.android.sdk.TestParameters;
import ru.yandex.money.android.sdk.TokenizationResult;

public class PayActivity extends AppCompatActivity {

    EditText amountPay;
    String payAmount;
    Button buttonPay;
    BigDecimal sum;
    Context c;
    private static final String TAG = "myLogs";
    private static final int REQUEST_CODE_TOKENIZE = 33;
    SharedPreferences settings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    public static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    public static final String APP_PREFERENCES_STATUS ="Status";
    public static final String APP_PREFERENCES_MSG ="Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        amountPay=(EditText)findViewById(R.id.amountPay);
        buttonPay=(Button)findViewById(R.id.buttonPay);
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        buttonPay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(amountPay.getText().toString().equals("")||amountPay.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    sum=new BigDecimal(amountPay.getText().toString());
                    timeToStartCheckout();
//                    Intent intent = new Intent(getApplicationContext(),
//                            MainActivity.class);
//                    startActivity(intent);
                }
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
                    TokenizationResult result = Checkout.createTokenizationResult(data);
                    Toast.makeText(getApplicationContext(),"Успешно",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Результат "+result);
//                    doPostRequest("http://192.168.252.199/card/add_money");

                case RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),"Отмена",Toast.LENGTH_SHORT).show();
                    doPostRequest("http://192.168.252.199/card/add_money");
                    // user canceled tokenization
                    break;
            }
        }
    }

    void timeToStartCheckout() {
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Пополнение карты",
                settings.getString(APP_PREFERENCES_CARD_CODE,"")+"",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856"
        );
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters);
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
        TestParameters testParameters = new TestParameters(true, true, new MockConfiguration(false, true, 5));
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters, testParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN,""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE,""));
            json.put("value",sum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
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

                        if(settings.contains(APP_PREFERENCES_STATUS)) {

                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                Toast.makeText(c,settings.getString(APP_PREFERENCES_MSG, "")
                                                ,Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(c,
                                        MainActivity.class);
                                startActivity(intent);
                            }
                        }

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }

}
