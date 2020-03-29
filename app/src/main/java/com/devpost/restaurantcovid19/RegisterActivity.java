package com.devpost.restaurantcovid19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreate;
    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAnAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        btnCreate = findViewById(R.id.btnCreate);
//
//        btnCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openLoginActivity();
//            }
//        });
        
        IntitializeFields();

        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });
    }

    private void IntitializeFields() {

        CreateAccountButton= findViewById(R.id.register_button);

        UserEmail = findViewById(R.id.register_email);
        UserPassword= findViewById(R.id.register_password);
        AlreadyHaveAnAccount= findViewById(R.id.already_have_link_account);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}
