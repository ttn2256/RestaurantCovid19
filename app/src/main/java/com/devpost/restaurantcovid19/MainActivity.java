package com.devpost.restaurantcovid19;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnUsers, btnOwners;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
//    private Button addBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //connect to xml
        btnUsers = findViewById(R.id.btnUser);
        btnOwners = findViewById(R.id.btnOwner);
//        addBusiness = findViewById(R.id.fab_add);
//        addBusiness.setVisibility(View.VISIBLE);

        //button owners function
        btnOwners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        //button user function
        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO need to change this logic
                mAuth.signOut();
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

        if(currentUser != null){
            Log.i("currentUser", currentUser.getUid());
        }

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
