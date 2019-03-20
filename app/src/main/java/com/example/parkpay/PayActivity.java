package com.example.parkpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Currency;

import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.MockConfiguration;
import ru.yandex.money.android.sdk.PaymentParameters;
import ru.yandex.money.android.sdk.TestParameters;
import ru.yandex.money.android.sdk.TokenizationResult;

public class PayActivity extends AppCompatActivity {
    EditText amountPay;
    Button buttonPay;
    BigDecimal sum;
    private static final String TAG = "myLogs";
    private static final int REQUEST_CODE_TOKENIZE = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        amountPay=(EditText)findViewById(R.id.amountPay);
        buttonPay=(Button)findViewById(R.id.buttonPay);
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
                    Toast.makeText(getApplicationContext(),"Успех "+result,Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Результат "+result);


                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),"Провал",Toast.LENGTH_SHORT).show();
                    // user canceled tokenization
                    break;
            }
        }
    }

    void timeToStartCheckout() {
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Название товара",
                "Описание товара",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856"
        );
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }
    void timeToStartCheckoutTest() {
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(sum, Currency.getInstance("RUB")),
                "Название товара",
                "Описание товара",
                "test_NTg4ODU2lc-4GywPaPNTcTbZG4ELvXgjk27aSrhbJ4U",
                "588856"
        );
        TestParameters testParameters = new TestParameters(true, true, new MockConfiguration(false, true, 5));
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters, testParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }

}
