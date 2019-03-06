package com.example.parkpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import static com.example.parkpay.QRActivity.ACTION_SCAN;

public class SingUpActivity extends AppCompatActivity {
    Button signUp;
    EditText name;
    EditText fam;
    EditText ot;
    EditText phone;
    EditText email;
    EditText pass;
    EditText numberCard;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singn_up);

        signUp=(Button) findViewById(R.id.signUp);
        name=(EditText) findViewById(R.id.name);
        fam=(EditText) findViewById(R.id.fam);
        ot=(EditText) findViewById(R.id.otc);
        phone=(EditText) findViewById(R.id.number);
        email=(EditText) findViewById(R.id.mail);
        pass=(EditText) findViewById(R.id.passw);
        numberCard=(EditText) findViewById(R.id.numberCard);


        numberCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (numberCard.getRight() - numberCard.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        scanQR(v);
                        return true;
                    }
                }
                return false;
            }
        });



        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")||name.getText().length() == 0||
                    fam.getText().toString().equals("")||fam.getText().length() == 0||
                    ot.getText().toString().equals("")||ot.getText().length() == 0||
                    phone.getText().toString().equals("")||phone.getText().length() == 0||
                    email.getText().toString().equals("")||email.getText().length() == 0||
                    pass.getText().toString().equals("")||pass.getText().length() == 0||
                    numberCard.getText().toString().equals("")||numberCard.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(SingUpActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }

            }
        });
    }


    // Запускаемм сканер штрих кода:
    public void scanBar(View v) {
        try {

            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(SingUpActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // Запуск сканера qr-кода:
    public void scanQR(View v) {
        try {

            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(SingUpActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // alert dialog для перехода к загрузке приложения сканера:
    private static AlertDialog showDialog(final Activity act, CharSequence title,
                                          CharSequence message,CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                // Ссылка поискового запроса для загрузки приложения:
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    // Обрабатываем результат, полученный от приложения сканера:
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // Получаем данные после работы сканера и выводим их в Toast сообщении:
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                numberCard.setText(contents);
//                Toast toast = Toast.makeText(this, "Содержание: " + contents + " Формат: " + format, Toast.LENGTH_LONG);
//                toast.show();

            }
        }
    }
}
