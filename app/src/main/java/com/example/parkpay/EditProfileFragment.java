package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EditProfileFragment extends Fragment {

    Button save;
    EditText name;
    EditText email;
    EditText phone;
    EditText dateBirthday;
    TextInputLayout mailLayout;

    Context c;

    final Calendar myCalendar = Calendar.getInstance();

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";

    SharedPreferences settings;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_edit_profile,container,false);

        save=(Button) view.findViewById(R.id.save);
        name=(EditText) view.findViewById(R.id.name);
        email=(EditText) view.findViewById(R.id.mail);
        phone=(EditText) view.findViewById(R.id.number);
        dateBirthday=(EditText) view.findViewById(R.id.dateBirthday);
        mailLayout=(TextInputLayout) view.findViewById(R.id.mailLayout);

        mailLayout.setHintEnabled(false);
        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

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

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
                editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
                editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
                editor.putString(APP_PREFERENCES_DATE_BIRTHDAY,dateBirthday.getText().toString());
                editor.apply();

                if (name.getText().toString().equals("")||name.getText().length() == 0||
                        email.getText().toString().equals("")||email.getText().length() == 0)
                {
                    Toast.makeText(c,"Поля: 'Имя' и 'Email' не должны быть пустыми!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    ((MainActivity) Objects.requireNonNull(getActivity()))
                            .replaceFragments(ProfileFragment.class);
                }
            }
        });

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

            name.setText(settings.getString(APP_PREFERENCES_NAME, ""));
            phone.setText(settings.getString(APP_PREFERENCES_NUMBER, ""));
            email.setText(settings.getString(APP_PREFERENCES_MAIL, ""));
            dateBirthday.setText(settings.getString(APP_PREFERENCES_DATE_BIRTHDAY, ""));
        }
        return view;
    }

    private void updateLabel() {
        String myFormat = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateBirthday.setText(sdf.format(myCalendar.getTime()));
    }
}
