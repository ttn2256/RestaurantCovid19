package com.devpost.restaurantcovid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class AddBusinessActivity extends AppCompatActivity {

    private AutocompleteSupportFragment autocompleteFragment;
    private String savedPlace = null, g, amPm, namePlace;
    private Double savedLat, savedLong;
    private GeoHash geoHash;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        btnRegister = findViewById(R.id.btnRegister);

        //button register function
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapsActivity();
            }
        });

        // get API key
        String apiKey = "AIzaSyC5tVRK4noWWEw7LgrfRpZ2LvM_otKNt7A";

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                namePlace = String.valueOf(place.getName());
                savedPlace = String.valueOf(place.getAddress());
                savedLat = place.getLatLng().latitude;
                savedLong = place.getLatLng().longitude;
                geoHash = new GeoHash(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });


    }

    public void openMapsActivity() {
        Intent intent = new Intent(AddBusinessActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}
