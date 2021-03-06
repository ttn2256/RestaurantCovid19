package com.devpost.restaurantcovid19;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreate;
    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAnAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();
        IntitializeFields();

        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }
    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToLoginActivity();
                                Toast.makeText(RegisterActivity.this,"Account created successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    private void IntitializeFields() {

        CreateAccountButton= findViewById(R.id.register_button);
        UserEmail = findViewById(R.id.register_email);
        UserPassword= findViewById(R.id.register_password);
        AlreadyHaveAnAccount= findViewById(R.id.already_have_link_account);
        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}