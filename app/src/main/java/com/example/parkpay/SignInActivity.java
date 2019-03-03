package com.example.parkpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {
Button signIn;
EditText login;
EditText pass;
TextView help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn=(Button) findViewById(R.id.signIn);
        login=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.password);
        help=(TextView) findViewById(R.id.helper);


        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (login.getText().toString().equals("")||login.getText().length() == 0||
                        pass.getText().toString().equals("")||pass.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(SignInActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SingUpActivity.class);
                startActivity(intent);

            }
        });
    }
}
