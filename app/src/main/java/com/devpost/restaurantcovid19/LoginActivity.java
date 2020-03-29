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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText txtEmail, txtPwd;
    TextView createAcc;
    Button btnLogin;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_2);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        /** Initialize all the fields of a current Business owner*/
        IntitalizeFields();
        /** Send to registration page when clicked on NeedNewAccount*/
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });
        /** Adding user to the database*/
        LoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

    }

    /**
     * Method that retrieves email and password and authenticate if successful. Throw an error otherwise.
     */
    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });
        }
    }



    /* Initializing Authentication page info*/
    private void IntitalizeFields() {
        LoginButton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword= findViewById(R.id.login_password);
        NeedNewAccountLink= findViewById(R.id.need_new_account_link);
        ForgetPasswordLink= findViewById(R.id.forget_password_link);
        loadingBar= new ProgressDialog(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(currentUser != null){
            openMapsActivity();
        }
    }

    /**
     * Sending a user to map view upon successful registration/login
     */
    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }
    /**
     * Sending a user to the registration page if clicked on "Need and account?"
     */
    private void SendUserToRegisterActivity() {
        Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
    }

    /**
     * Sends a user to the map view
     */
    public void openMapsActivity() {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }

}
