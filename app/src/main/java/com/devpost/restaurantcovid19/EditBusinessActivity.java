package com.devpost.restaurantcovid19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.devpost.restaurantcovid19.Model.Business;
import com.devpost.restaurantcovid19.Model.GeoEvents;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EditBusinessActivity extends AppCompatActivity {

    private AutocompleteSupportFragment autocompleteFragment;
    private String savedPlace = null, g, amPm, namePlace;
    private Double savedLat, savedLong;
    private GeoHash geoHash;
    private Button btnUpdate;
    private Spinner cuisineList, containerList;
    private ChipGroup chipGroup;
    private List<String> payment = new ArrayList<>();
    private Switch contactSwitch, itemSwitch, orderSwitch;
    private boolean contact = false, order = false, item = false;
    private RelativeLayout hidden;
    private TimePickerDialog timePickerDialog;
    private EditText nameEdit, certificateEdit, phoneEdit, urlEdit, startEdit, endEdit, etdEdit;
    private ImageButton btnContactLessInfo, btnSingleServiceInfo;
    private Calendar calendar;
    private int currentHour, currentMinute;
    private String nameString, certficateString, phoneString,
            urlString, startString, endString, etdString, street, scoreString;
    private TextInputLayout nameLayout, certificateLayout, phoneLayout, locationLayout, scoreLayout,
            startLayout, endLayout;
    private List<Double> l = new ArrayList<>();
    private List<String> chipData = new ArrayList<>();
    private DatabaseReference mDataBusiness, mDataGeoFire, mDataScore;
    private String[] methods = null, items = null, containers = null;
    private String restaurantID;
    private TextView scoreTV;
    private String[] placeSplit;
    private boolean error = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        //get the current intent
        Intent intent = getIntent();
        restaurantID = intent.getStringExtra("restaurantID");

        btnUpdate = findViewById(R.id.btnUpdate);
        cuisineList = findViewById(R.id.listCuisines);
        containerList = findViewById(R.id.containerList);
        chipGroup = findViewById(R.id.chipGroup);
        contactSwitch = findViewById(R.id.contactSwitch);
        itemSwitch = findViewById(R.id.itemSwitch);
        orderSwitch = findViewById(R.id.orderSwitch);
        nameEdit = findViewById(R.id.editName);
        certificateEdit = findViewById(R.id.certificate);
        phoneEdit = findViewById(R.id.contact);
        phoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        urlEdit = findViewById(R.id.url);
        startEdit = findViewById(R.id.startTime);
        endEdit = findViewById(R.id.endTime);
        etdEdit = findViewById(R.id.etd);
        btnContactLessInfo = findViewById(R.id.contactLessInfo);
        btnSingleServiceInfo = findViewById(R.id.serviceItemsInfo);
        scoreTV = findViewById(R.id.scoreTV);

        hidden = findViewById(R.id.hiddenLayout);
        nameLayout = findViewById(R.id.editNameLayOut);
        certificateLayout = findViewById(R.id.certLayOut);
        phoneLayout = findViewById(R.id.contactLayOut);
        startLayout = findViewById(R.id.startTimeLayOut);
        endLayout = findViewById(R.id.endTimeLayOut);
        locationLayout = findViewById(R.id.locationLayOut);
        scoreLayout = findViewById(R.id.scoreLayOut);

        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        // connect to firebase
        mDataBusiness = FirebaseDatabase.getInstance().getReference("Restaurants");
        mDataGeoFire = FirebaseDatabase.getInstance().getReference("GeoFire");
        mDataScore = FirebaseDatabase.getInstance().getReference("masterSheet");


        //button update function
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData() == false) {
                    saveInformation();
                    openMapsActivity();
                }

            }
        });

        // iamge button information
        btnSingleServiceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(EditBusinessActivity.this).create();
                alertDialog.setTitle("Single Service Articles Information");
                alertDialog.setMessage(
                        "Single-service articles means tableware, carry-out utensils, and other items such as bags, " +
                        "containers, placemats, stirrers, straws, " +
                        "toothpicks, and wrappers that are designed and constructed for one time, " +
                        "one person use after which they are intended for discard.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        btnContactLessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(EditBusinessActivity.this).create();
                alertDialog.setTitle("Contactless Delivery Information");
                alertDialog.setMessage("Customers can indicate where they would prefer their food order to be left by a " +
                                        " delivery person: whether that’s by the front door, at a reception desk or on a bench.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        //contactSwitch function
        contactSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hidden.setVisibility(View.VISIBLE);
                    contact = true;
                } else {
                    hidden.setVisibility(View.GONE);
                    contact = false;
                }
            }
        });

        //item Switch function
        itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    item = true;
                } else {
                    item = false;
                }
            }
        });

        //order switch function
        orderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    order = true;
                } else {
                    order = false;
                }
            }
        });

        //display drop down list
        //create a list of items for the spinner.
        items = new String[]{"American Food", "Indian Food", "Asian Food", "European Food"
                , "Latin Food", "African Food", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        cuisineList.setAdapter(adapter);

        //create a list of items for the spinner.
        containers = new String[]{"Recycled Paperboard", "Bio Plastic",
                "Plastic #2", "Plastic #5", "Brown Cardboard"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, containers);
        containerList.setAdapter(adapter1);


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

                placeSplit = savedPlace.split(",");
                street = placeSplit[0];
                scoreTV.setText("Retrieving score...");
                Query scoreDataQuery = mDataScore.orderByChild("2").equalTo(street).limitToFirst(1);
                scoreDataQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot s : dataSnapshot.getChildren()){
                                if (s.child("12").getValue().getClass().equals(String.class)) {
                                    String score = s.child("12").getValue(String.class);
                                    if (score.isEmpty()) {
                                        scoreTV.setText("N/A");
                                    } else {
                                        scoreTV.setText(score);
                                    }
                                } else {
                                    Long score = s.child("12").getValue(Long.class);
                                    scoreTV.setText(String.valueOf(score));
                                }

                            }
                        } else {
                            scoreTV.setText("N/A");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                savedLat = place.getLatLng().latitude;
                savedLong = place.getLatLng().longitude;
                geoHash = new GeoHash(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        // make sure the savedPlace value is updated when clear Text in the place autocomplete
        View clearButton = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocompleteFragment.setText("");
                savedPlace = null;
            }
        });

        // one click handle function
        startEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    timePickerDialog = new TimePickerDialog(EditBusinessActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if (hourOfDay >= 12) {
                                amPm = "PM";
                            } else {
                                amPm = "AM";
                            }
                            startEdit.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                        }
                    }, currentHour, currentMinute, false);

                    timePickerDialog.show();
                }
            }
        });


        // double click handle
        startEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(EditBusinessActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        startEdit.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        // one click handle
        endEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    timePickerDialog = new TimePickerDialog(EditBusinessActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if (hourOfDay >= 12) {
                                amPm = "PM";
                            } else {
                                amPm = "AM";
                            }
                            endEdit.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                        }
                    }, currentHour, currentMinute, false);

                    timePickerDialog.show();
                }
            }
        });

        // double click handle
        endEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(EditBusinessActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        endEdit.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        // display chip
        methods = new String[] {"Credit card", "Venmo", "Paypal", "Zelle",
                "Apple Pay", "Google Pay"};

        // display data
        final Query locationDataQuery = FirebaseDatabase.getInstance().getReference("Restaurants")
                .orderByKey().equalTo(restaurantID);
        locationDataQuery.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    final Business business = s.getValue(Business.class);
                    autocompleteFragment.setText(business.locationName);
                    namePlace = business.locationName;
                    savedPlace = business.address;
                    savedLat = business.getL().get(0);
                    savedLong = business.getL().get(1);
                    geoHash = new GeoHash(savedLat, savedLong);
                    scoreTV.setText(business.safetyScore);
                    nameEdit.setText(business.name);
                    certificateEdit.setText(business.cert);
                    cuisineList.setSelection(Arrays.asList(items).indexOf(business.cuisine));
                    phoneEdit.setText(business.phone);
                    urlEdit.setText(business.url);
                    startEdit.setText(business.start);
                    endEdit.setText(business.end);
                    orderSwitch.setChecked(business.orderFromCar);
                    contactSwitch.setChecked(business.contactLess);
                    etdEdit.setText(business.etd);
                    itemSwitch.setChecked(business.singleServiceItem);
                    containerList.setSelection(Arrays.asList(containers).indexOf(business.containerUsed));
                    for (String pm: business.payment) {
                        chipData.add(pm);
                    }
                    for(String method : methods) {
                        chipDisplay(method);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public void openMapsActivity() {
        Intent intent = new Intent(EditBusinessActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private boolean validateData() {

        certficateString = certificateEdit.getText().toString().trim();
        phoneString = phoneEdit.getText().toString().trim();
        startString = startEdit.getText().toString();
        endString = endEdit.getText().toString();
        etdString = etdEdit.getText().toString().trim();
        scoreString = scoreTV.getText().toString();

        if (savedPlace == null) {
            locationLayout.setError("Location can't be empty");
            error = true;
        } else {
            locationLayout.setError(null);
            error = false;
        }

        if (scoreString.equals("Retrieving score...")) {
            scoreLayout.setError("Please wait to retrieve score from database");
            return error = true;
        } else {
            scoreLayout.setError(null);
            error = false;
        }

        if (TextUtils.isEmpty(certficateString) || certficateString == null) {
            certificateLayout.setError("Certificate can't be empty");
            error = true;
        } else {
            certificateLayout.setError(null);
            error = false;
        }

        if (TextUtils.isEmpty(phoneString) || phoneString == null) {
            phoneLayout.setError("Phone can't be empty");
            error = true;
        } else {
            phoneLayout.setError(null);
            error = false;
        }

        if (TextUtils.isEmpty(startString) || startString == null) {
            startLayout.setError("Start hour can't be empty");
            error = true;
        } else {
            startLayout.setError(null);
            error = false;
        }

        if (TextUtils.isEmpty(endString) || endString == null) {
            endLayout.setError("End time can't be empty");
            error = true;
        } else {
            endLayout.setError(null);
            error = false;
        }

        return error;
    }


    private void chipDisplay(final String tag) {
        final Chip chip = new Chip(EditBusinessActivity.this);
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(EditBusinessActivity.this,
                null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setChipDrawable(chipDrawable);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setCloseIconVisible(false);
        chip.setPadding(60, 10, 60,10);
        chip.setText(tag);

        if (chipData != null && !chipData.isEmpty() && chipData.contains(tag)) {
            chip.setChecked(true);
            payment.add(tag);
        }

        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    payment.add(((ChipDrawable) chip.getChipDrawable()).getText().toString());
                } else {
                    payment.remove(((ChipDrawable) chip.getChipDrawable()).getText().toString());
                }
            }
        });
        chipGroup.addView(chip);
    }

    private void saveInformation() {
        nameString = nameEdit.getText().toString();
        urlString = urlEdit.getText().toString();
        l.add(savedLat);
        l.add(savedLong);
        g = geoHash.getGeoHashString();

        String cuisine = cuisineList.getSelectedItem().toString();
        String container = containerList.getSelectedItem().toString();
        Business businessInfo = new Business(nameString, namePlace, savedPlace,
                l, certficateString, cuisine, phoneString, urlString, startString,
                endString, order, contact, etdString, item, container, payment, scoreString);
        GeoEvents geoEvents = new GeoEvents(l, g);
        // add event to database
        mDataBusiness.child(restaurantID).setValue(businessInfo);
        // add geo information of the events into geo Fire
        mDataGeoFire.child(restaurantID).setValue(geoEvents);
    }
}
