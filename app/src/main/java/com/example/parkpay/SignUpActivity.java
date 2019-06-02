package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CheckBox;
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

    private TextInputEditText login;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText pass;
    private TextInputEditText confirmPassword;
    private TextInputEditText phone;
    private TextInputEditText dateBirthday;
    private TextInputEditText inviteCode;
    private TextInputLayout mailLabel;
    private TextInputLayout passwLabel;
    private TextInputLayout confirmPasswordLabel;
    private CheckBox agree;
    private AppCompatImageView openAgreeText;


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

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        login=findViewById(R.id.login);
        name= findViewById(R.id.name);
        email= findViewById(R.id.mail);
        pass= findViewById(R.id.passw);
        confirmPassword= findViewById(R.id.confirmPassword);
        phone= findViewById(R.id.number);
        dateBirthday= findViewById(R.id.dateBirthday);
        inviteCode= findViewById(R.id.inviteCode);
        AppCompatButton signUp = findViewById(R.id.signUp);
        ImageButton back = findViewById(R.id.back);
        AppCompatTextView signIn = findViewById(R.id.signIn);
        mailLabel= findViewById(R.id.mail_label);
        passwLabel= findViewById(R.id.passw_label);
        confirmPasswordLabel= findViewById(R.id.confirmPassword_label);
        agree= findViewById(R.id.agree);
        openAgreeText= findViewById(R.id.openAgreeText);

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

        openAgreeText.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(c,android.R.style.Theme_Material_Light_Dialog_NoActionBar);
//                    builder.setView();
            builder.setTitle(Html
                    .fromHtml("<font color='#000000'>Согласие на обработку персональных данных</font>"));
//            builder.setMessage(Html
//                    .fromHtml(getString(R.string.agreeText)));
            builder.setMessage(Html
                    .fromHtml("<div class=\"blog__hightlight\" color='#000000'>" +
                            "<p>" +
                            "Предоставляя свои персональные данные Пользователь даёт согласие на обработку, хранение и использование своих персональных данных на основании ФЗ № <nobr>152-ФЗ</nobr> «О персональных данных» от 27.07.2006 г. в следующих целях:" +
                            "</p>" +
                            "<ul" +
                            "<li>Осуществление клиентской поддержки</li>" +
                            "<li>Получения Пользователем информации о маркетинговых событиях</li>" +
                            "<li>Проведения аудита и прочих внутренних исследований с целью повышения качества предоставляемых услуг.</li>" +
                            "</ul>" +
                            "<p>" +
                            "Под персональными данными подразумевается любая информация личного характера, позволяющая установить личность Пользователя такая как:" +
                            "</p>" +
                            "<ul>" +
                            "<li>Фамилия, Имя, Отчество</li>" +
                            "<li>Дата рождения</li>" +
                            "<li>Контактный телефон</li>" +
                            "<li>Адрес электронной почты</li>" +
                            "<li>Почтовый адрес</li>" +
                            "</ul>" +
                            "<p>" +
                            "Персональные данные Пользователей хранятся исключительно на электронных носителях и обрабатываются с использованием автоматизированных систем, за исключением случаев, когда неавтоматизированная обработка персональных данных необходима в связи с исполнением требований законодательства." +
                            "</p>" +
                            "<p>" +
                            "ООО \"Феникс 48\" обязуется не передавать полученные персональные данные третьим лицам, за исключением следующих случаев:" +
                            "</p>" +
                            "<ul>" +
                            "<li>По запросам уполномоченных органов государственной власти РФ только по основаниям и в порядке, установленным законодательством РФ</li>" +
                            "<li>Стратегическим партнерам, которые работают с ООО \"Феникс 48\" для предоставления продуктов и услуг, или тем из них, которые помогают ООО \"Феникс 48\" реализовывать продукты и услуги потребителям. Мы предоставляем третьим лицам минимальный объем персональных данных, необходимый только для оказания требуемой услуги или проведения необходимой транзакции.</li>" +
                            "</ul>" +
                            "<p>" +
                            "ООО \"Феникс 48\" оставляет за собой право вносить изменения в одностороннем порядке в настоящие правила, при условии, что изменения не противоречат действующему законодательству РФ. Изменения условий настоящих правил вступают в силу после их публикации." +
                            "</p>" +
                            "</div>"
                    ));
            builder.setCancelable(false);
            builder.setPositiveButton("Согласен",
                    (dialog, id) -> agree.setChecked(true));
            builder.setNegativeButton("Отмена",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#3F51B5"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);


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
                if(!MainActivity.isValidEmail(Objects.requireNonNull(email.getText()).toString())){

                    mailLabel.setErrorEnabled(true);
                    mailLabel.setError(getResources().getString(R.string.error));
                }

                if(MainActivity.isValidEmail(email.getText().toString())){

                    email.getBackground().clearColorFilter();

                    mailLabel.setErrorEnabled(false);
                }

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

                passUser= Objects.requireNonNull(pass.getText()).toString();

                if(passUser.length()<6){

                    passwLabel.setErrorEnabled(true);
                    passwLabel.setError(getResources().getString(R.string.passError));
                }

                else{

                    pass.getBackground().clearColorFilter();

                    passwLabel.setErrorEnabled(false);
                }

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

                confirmPasswordUser= Objects.requireNonNull(confirmPassword.getText()).toString();
                passUser= Objects.requireNonNull(pass.getText()).toString();

                if(!passUser.equals(confirmPasswordUser)){



                    confirmPasswordLabel.setErrorEnabled(true);
                    confirmPasswordLabel.setError(getResources().getString(R.string.confirmError));
                }

                else{

                    confirmPassword.getBackground().clearColorFilter();

                    confirmPasswordLabel.setErrorEnabled(false);
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
            final int DRAWABLE_RIGHT = 2;

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

            loginUser= Objects.requireNonNull(login.getText()).toString();
            nameUser= Objects.requireNonNull(name.getText()).toString();
            emailUser= Objects.requireNonNull(email.getText()).toString();
            passUser= Objects.requireNonNull(pass.getText()).toString();
            confirmPasswordUser= Objects.requireNonNull(confirmPassword.getText()).toString();
            phoneUser= Objects.requireNonNull(phone.getText()).toString();
            dateBirthdayUser= Objects.requireNonNull(dateBirthday.getText()).toString();
            inviteCodeUser= Objects.requireNonNull(inviteCode.getText()).toString();

            if (
                    loginUser.equals("")||loginUser.length() == 0||
                    nameUser.equals("")||nameUser.length() == 0||
                    emailUser.equals("")||emailUser.length() == 0||
                    passUser.equals("")||passUser.length() == 0||
                    confirmPasswordUser.equals("")||confirmPasswordUser.length()==0||
                    !agree.isChecked()
            )
            {
                Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (passUser.equals(confirmPasswordUser)){

                    if(passUser.length()>=6) {

                        if (MainActivity.isValidEmail(email.getText().toString())) {

                            boolean checkConnection = MainActivity.isOnline(c);

                            if (checkConnection) {

                                signUp();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Отсутствует интернет соединение!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Неправильный адрес почты",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Пароль должен быть не менее 6 символов!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Пароли не совпадают",
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

    private void signUp(){

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
                .url("https://api.mobile.goldinnfish.com/register")
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
                        JSONObject Jobject = new JSONObject(Objects.requireNonNull(jsonData));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();

                        if(settings.contains(APP_PREFERENCES_STATUS)){

                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                Toast.makeText(c,"Для завершения регистрации," +
                                        " подтвердите свою электронную почту!",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(c,
                                        SignInActivity.class);
                                startActivity(intent);
                            }
                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "0")) {
                                Toast.makeText(c,Jobject.getString("msg"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);

//                        Toast.makeText(c,e+"",
//                                Toast.LENGTH_SHORT).show();
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
