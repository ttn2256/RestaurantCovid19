package com.devpost.restaurantcovid19;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.devpost.restaurantcovid19.Adapter.RestaurantInfoAdapter;
import com.devpost.restaurantcovid19.Model.Business;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerClickListener, GeoQueryEventListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private DatabaseReference mDataBusiness, mDataGeoFire;
    private static final int REQUEST_CODE = 101;
    private FloatingActionButton btnAdd;
    private Button btnEditInfo;
    private SlidingUpPanelLayout dragLayout;
    private TextView name, cuisine;
    private Button safetyGrade;
    private ListView listInfo;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //getting the logged in owner info
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setActionBar(toolbar);
        // connect to xm file
        btnAdd = findViewById(R.id.fab_add);
        btnAdd.setVisibility(View.VISIBLE);
        if(currentUser == null){
            btnAdd.setVisibility(View.GONE);
        }
        btnEditInfo = findViewById(R.id.btnEditInfo);
        name = findViewById(R.id.businessName);
        cuisine = findViewById(R.id.cuisine);
        safetyGrade = findViewById(R.id.safetyGrade);
        listInfo = findViewById(R.id.listInfo);

        dragLayout = findViewById(R.id.sliding_layout);

        // btn add function
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddActivity();
            }
        });

        // dragLayout click function
        dragLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dragLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        // connect to firebase
        mDataBusiness = FirebaseDatabase.getInstance().getReference("Restaurants");
        mDataGeoFire = FirebaseDatabase.getInstance().getReference("GeoFire");

        // retrieve current Location from the phone
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng curr = new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 14.0f));

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        mMap.setOnMarkerClickListener(this);

        mMap.setMyLocationEnabled(true);
        UiSettings settings = mMap.getUiSettings();
        settings.setRotateGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);

        // move myLocation button
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 300);

        getEventsInRadius();
    }

    /**
     * This function opens the Add Business Activity
     */
    public void openAddActivity() {
        Intent intent = new Intent(MapsActivity.this, AddBusinessActivity.class);
        startActivity(intent);
    }

    // For the three dot menu at the top of map
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
//         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    // Handling the logic when an owner will click on one of the options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.Logout){
             mAuth.signOut();
             SendUserToLoginActivity();
         }
         if(item.getItemId() == R.id.Edit){
             //TODO
             // We can send user to the Edit page here
         }
        return true;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    buildLocationRequest();
                    buildLocationCallBack();
                    currentLocation = location;
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    /**
     * This function takes the user location and check if user is near the event or not
     */
    public void buildLocationCallBack() {
        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                GeoFire geoFire = new GeoFire(mDataGeoFire);

                if (mMap != null) {
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()), 0.1f);

                    geoQuery.addGeoQueryEventListener(MapsActivity.this);
                }
            }
        };
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        dragLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        // disable editInfo button if users use create the event

        // display information from markers title as event id
        final Query locationDataQuery = FirebaseDatabase.getInstance().getReference("Restaurants")
                .orderByKey().equalTo(marker.getTitle());
        locationDataQuery.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    final Business business = s.getValue(Business.class);
                    if (business.locationName != null || !business.locationName.isEmpty()) {
                        name.setText(business.locationName);
                    } else {
                        name.setText(business.name);
                    }
                    if (business.cert.equals("A")) {
                        safetyGrade.setBackgroundColor(getResources().getColor(R.color.quantum_googgreen));
                    } else if (business.cert.equals("B")) {
                        safetyGrade.setBackgroundColor(getResources().getColor(R.color.quantum_yellow));
                    } else if (business.cert.equals("C")) {
                        safetyGrade.setBackgroundColor(getResources().getColor(R.color.quantum_googred));
                    } else {
                        safetyGrade.setBackgroundColor(getResources().getColor(R.color.quantum_grey));
                    }
                    cuisine.setText(business.cuisine);
                    safetyGrade.setText(business.cert);
                    safetyGrade.setTextSize(30);
                    safetyGrade.setTextColor(getResources().getColor(R.color.quantum_white_100));
                    // get name, location and category from firebase
                    String address = "", phone = "", url = "", hours = "", driveThru = "", contactLess = "",
                            singleItem = "", payment = "", etd = "";
                    address = business.address;
                    phone = business.phone;
                    url = business.url;
                    hours = business.start + " - " + business.end;
                    etd = business.etd;
                    List<String> paymentM = business.payment;

                    for (int i = 0; i < paymentM.size(); i++) {
                        if (i == paymentM.size() - 1) {
                            payment += paymentM.get(i);
                        } else {
                            payment += paymentM.get(i) + ", ";
                        }

                    }

                    if (business.orderFromCar == false) {
                        driveThru = "Drive Thru: No";
                    } else {
                        driveThru = "Drive Thru: Yes";
                    }
                    if (business.contactLess == false) {
                        contactLess = "Delivery: No";
                    } else {
                        if (business.singleServiceItem == false) {
                            singleItem = "Single Services Item: No";
                        } else {
                            singleItem = "Single Services Item: Yes";
                        }
                        contactLess = "Delivery: Yes \n \n"
                                + "Estimated Delivery Time: " + etd + " mins \n \n"
                                + singleItem + "\n \n"
                                + "Containers: " + business.containerUsed;
                    }
                    final String[] restaurantInfo = {address, phone, url, hours, driveThru, contactLess, payment};
                    Integer[] imgid = {
                            R.drawable.ic_location,
                            R.drawable.ic_phone,
                            R.drawable.ic_url,
                            R.drawable.ic_hours,
                            R.drawable.ic_car,
                            R.drawable.ic_contactless,
                            R.drawable.ic_payment
                    };
                    RestaurantInfoAdapter adapter = new RestaurantInfoAdapter(
                            MapsActivity.this, restaurantInfo, imgid);
                    listInfo.setAdapter(adapter);

                    listInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            if (position == 2) {
                                String selectedItem = restaurantInfo[position];
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem));
                                startActivity(intent);
                            } else if (position == 0) {
                                String selectedItem = "http://maps.google.co.in/maps?q=" + restaurantInfo[position];
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem));
                                startActivity(intent);
                            } else if (position == 1) {
                                String selectItem = "tel:" + restaurantInfo[position];
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse(selectItem));
                                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    ActivityCompat.requestPermissions(MapsActivity.this,
                                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                                    return;
                                }
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /* usersData.child(id(MapsActivity.this)).child("eventCurrent").child(marker.getTitle()).setValue(false);
                eventCapData.child(marker.getTitle()).child(id(MapsActivity.this)).setValue(true);
                eventCapData.child(marker.getTitle()).child(id(MapsActivity.this)).setValue(true);*/
                openEditBusinessActivity(marker.getTitle());
            }
        });

        return true;
    }

    public void openEditBusinessActivity(String restaurantID) {
        Intent intent = new Intent(MapsActivity.this, EditBusinessActivity.class);
        intent.putExtra("restaurantID", restaurantID);
        startActivity(intent);
    }

    /**
     * This functions get active events within 25 kilometers radius
     */
    public void getEventsInRadius() {

        GeoFire geoFire = new GeoFire(mDataGeoFire);

        // get geoFire data within 25 kilometers
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(),
                currentLocation.getLongitude()), 25);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Query locationDataQuery = FirebaseDatabase.getInstance().getReference("Restaurants").orderByKey().equalTo(key);
                locationDataQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //The dataSnapshot should hold the actual data about the location
                        for (DataSnapshot s : dataSnapshot.getChildren()){
                            Business business = s.getValue(Business.class);
                            double latitude = business.getL().get(0);
                            double longitude = business.getL().get(1);
                            String cuisine = business.cuisine;
                            String cert = business.cert;
                            LatLng location = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(location).title(s.getKey())
                                    .icon(BitmapDescriptorFactory.fromBitmap(addBorder(getCategoryIcon(cuisine), cert))));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    /**
     * This function gets icon depending on the category input
     * @param cuisine String
     * @return marker bitmap
     */
    public Bitmap getCategoryIcon (String cuisine) {
        int height = 100;
        int width = 100;
        int icon = 0;

        if (cuisine.toLowerCase().equals("american food")) {
            icon = R.mipmap.ic_american;
        } else if (cuisine.toLowerCase().equals("asian food")) {
            icon = R.mipmap.ic_asian;
        } else if (cuisine.toLowerCase().equals("indian food")) {
            icon = R.mipmap.ic_indian;
        } else if (cuisine.toLowerCase().equals("latin food")) {
            icon = R.mipmap.ic_latin;
        } else if (cuisine.toLowerCase().equals("european food")) {
            icon = R.mipmap.ic_europe;
        } else if (cuisine.toLowerCase().equals("others")) {
            icon = R.mipmap.ic_others;
        }

        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(MapsActivity.this, icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);

        return marker;
    }

    /**
     * This function creates stroke around icon
     * @param bitmap bitmap
     * @return output bitmap
     */
    private Bitmap addBorder(Bitmap bitmap, String cert) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        if (cert.equals("A")) {
            p.setColor(Color.GREEN);
        } else if (cert.equals("B")) {
            p.setColor(Color.YELLOW);
        } else {
            p.setColor(Color.RED);
        }
        p.setStrokeWidth(5);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {

    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

}
