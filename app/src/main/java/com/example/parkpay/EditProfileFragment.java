package com.example.parkpay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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

import static android.support.constraint.Constraints.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class EditProfileFragment extends Fragment {

//    Button save;
    EditText name;
    EditText email;
    EditText phone;
    EditText dateBirthday;
    String nameUser;
    String emailUser;
    String phoneUser;
    String dateBirthdayUser;
//    TextInputLayout mailLayout;
    ImageView backProfile;
    ImageView acceptSave;
    TextView changePhotoText;

    Context c;

    final Calendar myCalendar = Calendar.getInstance();

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    public static final String APP_PREFERENCES_STATUS ="Status";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PHOTO ="Photo";
    private static final String TAG = "myLogs";
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    SharedPreferences settings;


    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private ImageView photoProfile;

    private void takeCameraPicture() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {

            if (checkSelfPermission(c,Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }
            else {
                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
            }

        }
    }

    public void onActivityResult(int n, int n2, Intent intent) {
        if (n == 1 && n2 == -1) {
            Bitmap bitmap = (Bitmap)((Bundle)Objects.requireNonNull((Object)intent.getExtras())).get("data");
            photoProfile.setImageBitmap(bitmap);

            String img=encodeToBase64(bitmap);

            SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_PHOTO,img);
                editor.apply();
        }
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_edit_profile,container,false);

//        save=(Button) view.findViewById(R.id.save);
        name=(EditText) view.findViewById(R.id.name);
        email=(EditText) view.findViewById(R.id.mail);
        phone=(EditText) view.findViewById(R.id.number);
        dateBirthday=(EditText) view.findViewById(R.id.dateBirthday);
//        mailLayout=(TextInputLayout) view.findViewById(R.id.mailLayout);
        backProfile=(ImageView) view.findViewById(R.id.backProfile);
        acceptSave=(ImageView) view.findViewById(R.id.acceptSave);
        changePhotoText=(TextView) view.findViewById(R.id.changePhotoText);
        photoProfile=(ImageView) view.findViewById(R.id.photoProfile);

//        mailLayout.setHintEnabled(false);
        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(settings.contains(APP_PREFERENCES_PHOTO))
        {
            Bitmap bit=decodeToBase64(settings.getString(APP_PREFERENCES_PHOTO,""));
            photoProfile.setImageBitmap(bit);
        }

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
            public void afterTextChanged(Editable s) {

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

//        save.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                nameUser=name.getText().toString();
//                emailUser=email.getText().toString();
//                phoneUser=phone.getText().toString();
//                dateBirthdayUser=dateBirthday.getText().toString();
//
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
//                editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
//                editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
//                editor.putString(APP_PREFERENCES_DATE_BIRTHDAY,dateBirthday.getText().toString());
//                editor.apply();
//
//                if (nameUser.equals("")||nameUser.length() == 0||
//                        emailUser.equals("")||emailUser.length() == 0)
//                {
//                    Toast.makeText(c,"Поля: 'Имя' и 'Email' не должны быть пустыми!",
//                            Toast.LENGTH_SHORT).show();
//                }
//                else {
//
//                    boolean checkConnection=MainActivity.isOnline(c);
//
////                    if(checkConnection) {
//
//                        doPostRequest("http://192.168.252.199/user/edit");
////                    }
////                    else {
////                        Toast.makeText(c, "Отсутствует интернет соединение!",
////                                Toast.LENGTH_SHORT).show();
////                    }
//
//
//                }
//            }
//        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

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

        if(settings.contains(APP_PREFERENCES_NUMBER)&&settings.contains(APP_PREFERENCES_DATE_BIRTHDAY)
                &&settings.contains(APP_PREFERENCES_MAIL)&&settings.contains(APP_PREFERENCES_NAME)) {

            changePhotoText.setText(settings.getString(APP_PREFERENCES_NAME, ""));

            name.setText(settings.getString(APP_PREFERENCES_NAME, ""));
            email.setText(settings.getString(APP_PREFERENCES_MAIL, ""));
            phone.setText(settings.getString(APP_PREFERENCES_NUMBER, ""));
            dateBirthday.setText(settings.getString(APP_PREFERENCES_DATE_BIRTHDAY, ""));
        }

        backProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((MainActivity) Objects.requireNonNull(getActivity()))
                        .replaceFragments(ProfileFragment.class);

            }
        });

        acceptSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                nameUser=name.getText().toString();
                emailUser=email.getText().toString();
                phoneUser=phone.getText().toString();
                dateBirthdayUser=dateBirthday.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
                editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
                editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
                editor.putString(APP_PREFERENCES_DATE_BIRTHDAY,dateBirthday.getText().toString());
                editor.apply();

                if (nameUser.equals("")||nameUser.length() == 0||
                        emailUser.equals("")||emailUser.length() == 0)
                {
                    Toast.makeText(c,"Поля: 'Имя' и 'Email' не должны быть пустыми!",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    boolean checkConnection=MainActivity.isOnline(c);

//                    if(checkConnection) {

                    doPostRequest("http://192.168.252.199/user/edit");
//                    }
//                    else {
//                        Toast.makeText(c, "Отсутствует интернет соединение!",
//                                Toast.LENGTH_SHORT).show();
//                    }


                }
            }
        });

        changePhotoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCameraPicture();
            }
        });


        return view;
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);

            } else {

                Toast.makeText(c, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    private void updateLabel() {
        String myFormat = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateBirthday.setText(sdf.format(myCalendar.getTime()));
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
            json.put("name",nameUser);
            json.put("email",emailUser);
            json.put("phone",phoneUser);
            json.put("birthday",dateBirthdayUser);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        Log.d(TAG,json.toString());
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        Log.d(TAG,request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject Jobject = new JSONObject(jsonData);

                            Log.d(TAG, Jobject.getString("status"));
                            Log.d(TAG, Jobject.getString("msg"));

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(APP_PREFERENCES_STATUS, Jobject.getString("status"));
                            editor.apply();

                            if(settings.contains(APP_PREFERENCES_STATUS)){
                                if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                    Toast.makeText(c,"Сохранение",Toast.LENGTH_SHORT).show();

                                    ((MainActivity) Objects.requireNonNull(getActivity()))
                                            .replaceFragments(ProfileFragment.class);
                                }
                                else {

                                    Toast
                                            .makeText(c,Jobject.getString("msg"),Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}
