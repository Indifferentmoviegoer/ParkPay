package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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

public class SignUpActivity extends AppCompatActivity {

    private AppCompatButton signUp;
    private TextInputEditText login;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText pass;
    private TextInputEditText confirmPassword;
    private TextInputEditText phone;
    private TextInputEditText dateBirthday;
    private TextInputEditText inviteCode;
    private ImageButton back;
    private AppCompatTextView signIn;


    private String loginUser;
    private String nameUser;
    private String emailUser;
    private String passUser;
    private String confirmPasswordUser;
    private String phoneUser;
    private String dateBirthdayUser;
    private String inviteCodeUser;
    private Context c;

    private final Calendar myCalendar = Calendar.getInstance();

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_LOGIN ="Login";
    private static final String APP_PREFERENCES_NAME ="Name";
    private static final String APP_PREFERENCES_MAIL ="Email";
    private static final String APP_PREFERENCES_PASS ="Password";
    private static final String APP_PREFERENCES_NUMBER ="Number";
    private static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MSG ="Message";
    private static final String TAG = "myLogs";
    private SharedPreferences settings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.MaterialTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login=findViewById(R.id.login);
        name= findViewById(R.id.name);
        email= findViewById(R.id.mail);
        pass= findViewById(R.id.passw);
        confirmPassword= findViewById(R.id.confirmPassword);
        phone= findViewById(R.id.number);
        dateBirthday= findViewById(R.id.dateBirthday);
        inviteCode= findViewById(R.id.inviteCode);
        signUp= findViewById(R.id.signUp);
        back= findViewById(R.id.back);
        signIn= findViewById(R.id.signIn);


        c=this;

        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("___________");
        MaskImpl mask = MaskImpl.createTerminated(slots);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(phone);

        back.setOnClickListener(view -> {

            Intent intent = new Intent(c,
                    SignInActivity.class);
            startActivity(intent);

        });

        signIn.setOnClickListener(view -> {

            Intent intent = new Intent(c,
                    SignInActivity.class);
            startActivity(intent);

        });

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

//                    mailLayout.setErrorEnabled(true);
//                    mailLayout.setHintEnabled(false);
//                    mailLayout.setError(getResources().getString(R.string.mailLayout));
                }

                if(MainActivity.isValidEmail(email.getText().toString())){

                    email.getBackground().clearColorFilter();

//                    mailLayout.setErrorEnabled(false);
                }

            }
        });

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        dateBirthday.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (dateBirthday.getRight() - dateBirthday.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    new DatePickerDialog(
                            c,
                            R.style.MyDatePickerDialogTheme,
                            date,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                    ).show();

                    return true;
                }
            }
            return false;
        });

        signUp.setOnClickListener(v -> {

            loginUser=login.getText().toString();
            nameUser=name.getText().toString();
            emailUser=email.getText().toString();
            passUser=pass.getText().toString();
            confirmPasswordUser=confirmPassword.getText().toString();
            phoneUser=phone.getText().toString();
            dateBirthdayUser=dateBirthday.getText().toString();
            inviteCodeUser=inviteCode.getText().toString();

            if (
                    loginUser.equals("")||loginUser.length() == 0||
                    nameUser.equals("")||nameUser.length() == 0||
                    emailUser.equals("")||emailUser.length() == 0||
                    passUser.equals("")||passUser.length() == 0||
                    confirmPasswordUser.equals("")||confirmPasswordUser.length()==0||
                    inviteCodeUser.equals("")||inviteCodeUser.length()==0
            )
            {
                Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (passUser.equals(confirmPasswordUser)){

                    boolean checkConnection=MainActivity.isOnline(c);

                        if(checkConnection) {

                        doPostRequest("https://api.mobile.goldinnfish.com/register");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
                                    Toast.LENGTH_SHORT).show();
                        }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Пароли не совпадают",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_LOGIN,loginUser);
        editor.putString(APP_PREFERENCES_NAME,nameUser);
        editor.putString(APP_PREFERENCES_NUMBER,phoneUser);
        editor.putString(APP_PREFERENCES_MAIL,emailUser);
        editor.putString(APP_PREFERENCES_PASS,passUser);
        editor.putString(APP_PREFERENCES_DATE_BIRTHDAY,dateBirthdayUser);
        editor.apply();
    }

    private void updateLabel() {
        String myFormat = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateBirthday.setText(sdf.format(myCalendar.getTime()));
    }

    private void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {

            json.put("login",loginUser);
            json.put("name",nameUser);
            json.put("email",emailUser);
            json.put("password",passUser);
            json.put("password_confirmation",confirmPasswordUser);
            json.put("phone",phoneUser);
            json.put("birthday",dateBirthdayUser);
            json.put("invite_code",inviteCodeUser);

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

                        Log.d(TAG,Jobject.getString("status"));
                        Log.d(TAG,Jobject.getString("msg"));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();

                        if(settings.contains(APP_PREFERENCES_STATUS)){

                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                Toast.makeText(c,"Вход",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(c,
                                        MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(c,settings.getString(APP_PREFERENCES_MSG, ""),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(c,
                SignInActivity.class);
        startActivity(intent);

        finish();
    }
}
