package com.devpost.restaurantcovid19;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText txtEmail, txtPwd;
    TextView createAcc;
    Button btnLogin;
    private FirebaseUser currentUser;
    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        btnLogin = findViewById(R.id.btnLogin);
//        txtEmail = findViewById(R.id.emailLogin);
//        txtPwd = findViewById(R.id.pwdLogin);
//        createAcc = findViewById(R.id.txtCreateAcc);
        

//        //button login function
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMapsActivity();
//            }
//        });
//
//        //text view on click
//        createAcc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openRegisterActivity();
//            }
//        });
        IntitalizeFields();
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

    }

    private void IntitalizeFields() {
        LoginButton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword= findViewById(R.id.login_password);
        NeedNewAccountLink= findViewById(R.id.need_new_account_link);
        ForgetPasswordLink= findViewById(R.id.forget_password_link);


    }

    @Override
    public void onStart() {
        super.onStart();
        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent logingIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(logingIntent);
    }
    private void SendUserToRegisterActivity() {
        Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
    }


    public void openMapsActivity() {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
