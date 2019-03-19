package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

//import static com.example.parkpay.QRActivity.ACTION_SCAN;

public class SignUpActivity extends AppCompatActivity {
    Button signUp;
    EditText login;
    EditText name;
    EditText email;
    EditText pass;
    EditText confirmPassword;
    EditText phone;
    EditText dateBirthday;
    TextInputLayout mailLayout;
    Context c;
    final Calendar myCalendar = Calendar.getInstance();
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LOGIN ="Login";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_PASS ="Password";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    public static final String APP_PREFERENCES_TOKEN ="Token";

    SharedPreferences settings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singn_up);

        login=findViewById(R.id.login);
        name= findViewById(R.id.name);
        email= findViewById(R.id.mail);
        pass= findViewById(R.id.passw);
        confirmPassword= findViewById(R.id.confirmPassword);
        phone= findViewById(R.id.number);
        dateBirthday= findViewById(R.id.dateBirthday);
        signUp= findViewById(R.id.signUp);
        mailLayout=(TextInputLayout) findViewById(R.id.mailLayout);
        mailLayout.setHintEnabled(false);
        c=this;
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___________");
        MaskImpl mask = MaskImpl.createTerminated(slots);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(phone);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!MainActivity.isValidEmail(email.getText().toString())){

                    email.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

                    mailLayout.setErrorEnabled(true);
                    mailLayout.setHintEnabled(false);
                    mailLayout.setError(getResources().getString(R.string.mailLayout));
                }

                if(MainActivity.isValidEmail(email.getText().toString())){

                    email.getBackground().clearColorFilter();

                    mailLayout.setErrorEnabled(false);
                }

            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateBirthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (dateBirthday.getRight() - dateBirthday.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        new DatePickerDialog(c, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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
                    email.getText().toString().equals("")||email.getText().length() == 0||
                    pass.getText().toString().equals("")||pass.getText().length() == 0||
                    confirmPassword.getText().toString().equals("")||confirmPassword.getText().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (pass.getText().toString().equals(confirmPassword.getText().toString())){
//                        JSONObject json = new JSONObject();
//                        try {
//                            json.put("login","");
//                            json.put("password","");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        MainActivity.doGetRequest("http://www.mobile.ru/register",
//                                json,c,settings,SignUpActivity.this);
                        Intent intent = new Intent(c,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Пароли не совпадают",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
        editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
        editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
        editor.putString(APP_PREFERENCES_PASS,pass.getText().toString());
        editor.putString(APP_PREFERENCES_DATE_BIRTHDAY,dateBirthday.getText().toString());
        editor.apply();
    }

    private void updateLabel() {
        String myFormat = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateBirthday.setText(sdf.format(myCalendar.getTime()));
    }

}
