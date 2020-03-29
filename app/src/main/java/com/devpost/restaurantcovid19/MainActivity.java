package com.devpost.restaurantcovid19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnUsers, btnOwners;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect to xml
        btnUsers = findViewById(R.id.btnUser);
        btnOwners = findViewById(R.id.btnOwner);

        //button owners function
        btnOwners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });


        //button user function
        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapsActivity();
            }
        });
    }


    public void openMapsActivity() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {

        super.onStart();
        if(currentUser == null){
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent logingIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logingIntent);
    }
}
