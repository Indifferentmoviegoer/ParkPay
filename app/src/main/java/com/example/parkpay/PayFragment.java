package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
import ru.yandex.money.android.sdk.PaymentMethodType;
import ru.yandex.money.android.sdk.PaymentParameters;
import ru.yandex.money.android.sdk.TestParameters;
import ru.yandex.money.android.sdk.TokenizationResult;
import ru.yandex.money.android.sdk.UiParameters;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PayFragment extends Fragment {

    private EditText amountPay;
    private ContentLoadingProgressBar progressPay;
    private AppCompatButton buttonPay;
    private AppCompatTextView titlePay;
    private AppCompatTextView numberPay;
    private AppCompatTextView titleAmount;
    private AppCompatImageView imPay;

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
    private static final String CONFIRM = "confirm";

    private TokenizationResult result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pay, container, false);

        if (container != null) {
            c = container.getContext();
        }

        amountPay = view.findViewById(R.id.amountPay);
        buttonPay = view.findViewById(R.id.buttonPay);
        titlePay = view.findViewById(R.id.titlePay);
        numberPay = view.findViewById(R.id.numberPay);
        imPay = view.findViewById(R.id.imPay);
        progressPay = view.findViewById(R.id.progressPay);
        titleAmount = view.findViewById(R.id.titleAmount);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Glide.with(c)
                .load(R.drawable.pay)
                .thumbnail(0.5f)
                .dontAnimate()
                .into(imPay);

        progressPay.setVisibility(View.INVISIBLE);

        String name = settings.getString(APP_PREFERENCES_CARD_NAME, "") + "";
        String number = settings.getString(APP_PREFERENCES_CARD_CODE, "") + "";

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("____");
        MaskImpl mask = MaskImpl.createTerminated(slots);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(amountPay);

        titlePay.setText(name);
        numberPay.setText(number);

        buttonPay.setOnClickListener(v -> {

            if (amountPay.getText().toString().equals("") || amountPay.getText().length() == 0) {
                Toast.makeText(c, "Введите сумму",
                        Toast.LENGTH_SHORT).show();
            } else {
                sum = new BigDecimal(amountPay.getText().toString());
                timeToStartCheckout();
            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TOKENIZE) {
            switch (resultCode) {
                case RESULT_OK:

                    result = Checkout.createTokenizationResult(data);

                    if(result.getPaymentMethodType()== PaymentMethodType.BANK_CARD){


                        boolean checkConnection=MainActivity.isOnline(c);

                        if(checkConnection) {


                        progressPay.setVisibility(View.VISIBLE);
                        buttonPay.setVisibility(View.INVISIBLE);
                        titlePay.setVisibility(View.INVISIBLE);
                        numberPay.setVisibility(View.INVISIBLE);
                        imPay.setVisibility(View.INVISIBLE);
                        amountPay.setVisibility(View.INVISIBLE);
                        titleAmount.setVisibility(View.INVISIBLE);

                        Toast.makeText(c, "Пожалуйста, подождите!", Toast.LENGTH_LONG).show();

                        createPayment();

                        } else {
                            Toast.makeText(c, "Отсутствует интернет соединение!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {

                        boolean checkConnection=MainActivity.isOnline(c);

                        if(checkConnection) {

                        Toast.makeText(c, "Успешно", Toast.LENGTH_LONG).show();
                        createPayment();

                        } else {
                            Toast.makeText(c, "Отсутствует интернет соединение!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                case RESULT_CANCELED:
                    break;
            }
        }
    }

    private void timeToStartCheckout() {

        final Set<PaymentMethodType> paymentMethodTypes = new HashSet<>();
        paymentMethodTypes.add(PaymentMethodType.BANK_CARD);

        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Пополнение карты",
                settings.getString(APP_PREFERENCES_CARD_CODE,"")+"",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856",
                paymentMethodTypes
        );

        UiParameters uiParameters = new UiParameters(false, new ColorScheme(Color.parseColor("#3F51B5")));

        Intent intent = Checkout.createTokenizeIntent(c, paymentParameters,new TestParameters(),uiParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }

    private void createPayment(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
//            json.put("token",settings.getString(APP_PREFERENCES_TOKEN,""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE,""));
            json.put("value",sum);
            json.put("token",result.getPaymentToken());
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
                .url("https://api.mobile.goldinnfish.com/card/create_payment")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject parentObject = new JSONObject(Objects.requireNonNull(jsonData));
                            JSONObject Jobject = parentObject.getJSONObject("confirmation");


                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(CONFIRM, Jobject.getString("confirmation_url"));
                            editor.apply();

                            Intent intent = new Intent(getContext(),
                                    ConfirmActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).overridePendingTransition(0, 0);

                            progressPay.setVisibility(View.INVISIBLE);
                            buttonPay.setVisibility(View.VISIBLE);
                            titlePay.setVisibility(View.VISIBLE);
                            numberPay.setVisibility(View.VISIBLE);
                            imPay.setVisibility(View.VISIBLE);
                            amountPay.setVisibility(View.VISIBLE);
                            titleAmount.setVisibility(View.VISIBLE);

                        } catch (IOException | JSONException e) {

                            Log.d(TAG, "Ошибка: " + e);
                        }
                    });
                }
            }
        });

    }
}