package com.example.parkpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cards.pay.paycardsrecognizer.sdk.Card;
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent;
import ru.yandex.money.android.sdk.Checkout;

public class ScanBankCardActivity extends Activity {

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new ScanCardIntent.Builder(this).build();
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                final Card card = data.getParcelableExtra(ScanCardIntent.RESULT_PAYCARDS_CARD);
                final String expirationDate = card.getExpirationDate();

                if (expirationDate != null) {
                    final String[] expirationDateParts = expirationDate.split("/");
                    final Intent scanBankCardResult = Checkout.createScanBankCardIntent(
                            card.getCardNumber(),
                            Integer.parseInt(expirationDateParts[0]),
                            Integer.parseInt(expirationDateParts[1]));
                    setResult(RESULT_OK, scanBankCardResult);
                } else {
                    setResult(RESULT_CANCELED);
                }
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }

//    private void onScanningDone(final String cardNumber, final int expirationMonth, final int expirationYear) {
//
//        //final Intent result = Checkout.createScanBankCardResult(cardNumber, expirationMonth, expirationYear);
//        final Intent result = Checkout.createScanBankCardIntent(cardNumber, expirationMonth, expirationYear);
//
//        setResult(Activity.RESULT_OK, result);
//
//        finish();
//
//    }

}
