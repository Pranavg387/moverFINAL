package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class customerMapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int GPS_REQUEST_CODE = 9003;
    boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;

    private EditText mSearchAddress;

    private int Status = 0;

    public static final int DEFAULT_ZOOM = 15;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;

    private LatLng pickupLocation, destinationLatLng;

    private List<Polyline> polylines = null;
    private Button requestRide, mSettings, mBtnLocate;
    private long backPressedTime;
    private Toast backToast;
    Marker mCustomerMarker = null;
    Marker mDestinationMarker = null;
    private Boolean requestBol = false;
    private TextView cancelRide;
    String requestService;
    private LinearLayout mDriverInfo;

    private ImageView mDriverProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    private RadioGroup mRadioGroup;
    private RatingBar mRatingBar;
    private DrawerLayout mNavDrawer;

    public static final String rec_drive_can_s="rec_drive_can_s";
    private String rec_driv_can = null;

    String newS;
    String newT;
    int diff;
    loadingDialog mloadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout_c);

        //***************TOOLBAR**********************

        Toolbar toolbar_d = findViewById(R.id.toolbar_c);
        setSupportActionBar(toolbar_d);
        getSupportActionBar().setTitle(null);


        mNavDrawer = findViewById(R.id.drawer_layout_c);
        NavigationView navigationView = findViewById(R.id.navigation_view_c);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, toolbar_d, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // **************************


        //LOADING SCREEN

        mloadingDialog = new loadingDialog(customerMapsActivity.this);

        //



        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        driverFoundID = sharedpreferences.getString(driverFoundId_s, null);
        rec_driv_can = sharedpreferences.getString(rec_drive_can_s,null);

        requestBol = sharedpreferences.getBoolean("req_bol",false);

        Log.d("adam", String.valueOf(requestBol));


        destination = "";
        mSearchAddress = findViewById(R.id.et_address);
        mBtnLocate = findViewById(R.id.btn_locate);
        mBtnLocate.setOnClickListener(this::geoLocate);

        cancelRide = findViewById(R.id.cancelRide);

        destinationLatLng = new LatLng(0.0, 0.0);
        mDriverInfo = findViewById(R.id.driverInfo);

        mDriverProfileImage = findViewById(R.id.driverProfileImage);

        mDriverName = findViewById(R.id.driverName);
        mDriverPhone = findViewById(R.id.driverPhone);
        mDriverCar = findViewById(R.id.driverCar);

        mRatingBar = findViewById(R.id.ratingBar);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.Truck);



        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

       supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        initGoogleMap();

if(rec_driv_can!=null) {
Log.d("XXXR",String.valueOf(rec_driv_can));
    Log.d("XXXR",String.valueOf(driverFoundID));

    newS = rec_driv_can.split("#", 2)[0];
    newT = rec_driv_can.split("#", 2)[1];
}

        requestRide = findViewById(R.id.requestRide);


        requestRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol) {
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();

                    editor1.putString(rec_drive_can_s ,driverFoundID+ "#"+ getCurrentTimestamp());

                    requestBol=false;
                    editor1.putBoolean("req_bol", false);

                    editor1.apply();

                    rec_driv_can = sharedpreferences.getString(rec_drive_can_s,null);
                    Log.d("requestbol : ", requestBol.toString());
                    Log.d("rec : ", rec_driv_can.toString());

                    newS = rec_driv_can.split("#", 2)[0];
                    newT = rec_driv_can.split("#", 2)[1];

                    Date Date2 = null;

                    Date Date1 = null;
                    try {
                        Date2 = sdf.parse(newT);
                        Date1 = sdf.parse(getCurrentTimestamp());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long millse = Date1.getTime() - Date2.getTime();
                    long mills = Math.abs(millse);

                    int Hours = (int) (mills / (1000 * 60 * 60));
                    int Mins = (int) (mills / (1000 * 60)) % 60;
                    long Secs = (int) (mills / 1000) % 60;

                    diff = Mins;
                    Log.d("ADAMS", "Time" + diff);


                    endRide();


                } else {
                    if (destination != ""   ) {


                        Toast.makeText(customerMapsActivity.this, "Press Again to Cancel", Toast.LENGTH_SHORT).show();
                        requestRide.setText("Looking for Driver Location....");
                        int selectId = mRadioGroup.getCheckedRadioButtonId();

                        final RadioButton radioButton = findViewById(selectId);

                        if (radioButton.getText() == null) {
                            return;
                        }

                        requestService = radioButton.getText().toString();
                        //requestBol = true;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("req_bol", true);
                        editor.apply();
                        requestBol =true;

                        if (ActivityCompat.checkSelfPermission(customerMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(customerMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Location location = task.getResult();

                                gotoLocation(location.getLatitude(), location.getLongitude());
                                pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.d("MAP_DEBUG", "pickup Location " + pickupLocation);

                                getCurrentLocation();
                                // getLocationUpdate();

                                //Send to Database
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

                                GeoFire geoFire = new GeoFire(ref);
                                geoFire.setLocation(userId, new GeoLocation(pickupLocation.latitude, pickupLocation.longitude));

                                //LOOK FOR DRIVERS
                                getClosestDriver();


                            }
                        });
                    } else {
                        Toast.makeText(customerMapsActivity.this, "Enter Destination", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });







        mLocationClient = new FusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for(  Location location : locationResult.getLocations()){
                    if(getApplicationContext()!=null) {
                        pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        //Toast.makeText(DriverMapsActivity.this, "LocationResult: Location is " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        Log.d("MAP_DEBUG", "onLocationResult: Location is " + location.getLatitude() + " ," + location.getLongitude());
                        showMarker(location.getLatitude(), location.getLongitude());

                    }}
            }
        };
        if (driverFoundID != null) {
            try {


                getDriverInfo();
                getDriverLocation();
                getHasRideEnded();



            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }




    // FIND NEAREST DRIVERS

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID = null;
    private GeoQuery geoQuery;
    //shared preferences
    public static final String MyPREFERENCES = "com.ex.mover_f";

    public static final String driverFoundId_s = "driverFoundId_s";
    SharedPreferences sharedpreferences;

    DatabaseReference driverLocation;


    private void getClosestDriver() {
        if(driverFoundID==null){
            driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable"); }
        else { driverLocation = FirebaseDatabase.getInstance().getReference().child("driversWorking");}

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol) {

                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound) {
                                    return;
                                }

                                if(rec_driv_can!=null) {
                                    //  Toast.makeText(customerMapsActivity.this, "Time" + ((Integer.parseInt(getCurrentTimestamp()) - Integer.parseInt(rec_driv_can.split("$", 2)[1])) / 60), Toast.LENGTH_SHORT).show();
                                }


                                Log.d("ADAM", String.valueOf(diff));

                                if (driverMap.get("service").equals(requestService) && ((diff>5 && newS.equals(dataSnapshot.getKey()))|| !(dataSnapshot.getKey().equals(newS))) ) {
                                    driverFound = true;
                                    Log.d("adamDiff", String.valueOf(diff));
                                    Log.d("adamID", String.valueOf(newS));


                                    driverFoundID = dataSnapshot.getKey();




                                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                    editor1.putString(rec_drive_can_s, null);
                                    editor1.apply();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    Toast.makeText(customerMapsActivity.this, "id going in sp :" + driverFoundID, Toast.LENGTH_SHORT).show();
                                    editor.putString(driverFoundId_s, driverFoundID);
                                    editor.apply();


                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    if (destinationLatLng != null) {
                                        map.put("destinationLat", destinationLatLng.latitude);
                                        map.put("destinationLng", destinationLatLng.longitude);
                                        driverRef.updateChildren(map);
                                    } else {
                                        Toast.makeText(customerMapsActivity.this, "Choose a Destination", Toast.LENGTH_SHORT).show();
                                    }


                                    mloadingDialog.startLoadingAnimation();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mloadingDialog.dismissDialog();
                                        }
                                    }, 10000);


                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();
                                    requestRide.setText("Looking for Driver Location....");

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound && (radius<=10)) { //Changed


                    radius++;

                    getClosestDriver();

                   /* if(radius>10) {
                        Toast.makeText(customerMapsActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                    endRide();
                    }*/
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

      /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/

    private void getDriverInfo() {
        // FOUNDID = NULL // Toast.makeText(this, "DriverFoundID" + driverFoundID, Toast.LENGTH_SHORT).show();
        if(driverFoundID!=null){
            DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Status = 1;
                    mRadioGroup.setVisibility(View.GONE);
                    mSearchAddress.setVisibility(View.GONE);
                    mBtnLocate.setVisibility(View.GONE);


                    mDriverInfo.setVisibility(View.VISIBLE);

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("name") != null) {
                            mDriverName.setText("Name:" + map.get("name").toString());
                        }
                        if (map.get("phone") != null) {
                            mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                        }
                        if (map.get("car") != null) {
                            mDriverCar.setText("Car :" + map.get("car").toString());
                        }
                        if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                            Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                        }

                        int ratingSum = 0;
                        float ratingsTotal = 0;
                        float ratingsAvg = 0;
                        for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                            ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                            ratingsTotal++;
                        }
                        if (ratingsTotal != 0) {
                            ratingsAvg = ratingSum / ratingsTotal;
                            mRatingBar.setRating(ratingsAvg);
                        }



                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });}
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/

    Marker mDriverMarker;
    DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        if(driverFoundID!=null){
            driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
            driverLocationRefListener= driverLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists() ){
                        List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double locationLat = 0;
                        double locationLng = 0;
                        requestRide.setText("Driver Found");

                        //
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        if(pickupLocation!=null) {
                            DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                            GeoFire geoFire1 = new GeoFire(assignedCustomerRef);
                            geoFire1.setLocation("pickLocation", new GeoLocation(pickupLocation.latitude, pickupLocation.longitude));
                        }

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.removeLocation(userId);

                        //

                        if(map.get(0) != null){
                            locationLat = Double.parseDouble(map.get(0).toString());
                        }

                        if(map.get(1) != null){
                            locationLng = Double.parseDouble(map.get(1).toString());
                        }

                        LatLng driverLatLng = new LatLng(locationLat,locationLng);

                        if(mDriverMarker != null){
                            mDriverMarker.remove();
                        }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        requestRide.setText("Driver is Here");
                    }else{
                        requestRide.setText("Driver Found" + " "+ distance +"m Away");
                    }


                        if(mDriverMarker != null){
                            mDriverMarker.remove();
                        }
                        mDriverMarker =mGoogleMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver"));


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });}


    }

    /*-------------------------------------------- WORKING FUNCTIONALITY-----
    |  Function(s) getRouteToMarker,endRide,onBackPressed, *OnPause*
    | geoLocate,onStop,getHasRideEnded
    |
    |  Purpose:  Help App operate and perform functions
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/

    String destination;
    private void geoLocate(View view) {



        hideSoftKeyboard(view);

        destination = mSearchAddress.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(destination, 3);

            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                gotoLocation(address.getLatitude(), address.getLongitude());

                //showMarker
                if(mDestinationMarker != null){
                    mDestinationMarker.remove();
                }
                mDestinationMarker =mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Destination"));


                // Toast.makeText(this, address.getLocality(), Toast.LENGTH_SHORT).show();

                Log.d("MAP_DEBUG", "geoLocate: Locality: " + address.getLocality());
            }

            for (Address address : addressList) {
                Log.d("MAP_DEBUG", "geoLocate: Address: " + address.getAddressLine(address.getMaxAddressLineIndex()));
            }


        } catch (IOException e) {


        }


    }



    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    private void getHasRideEnded(){ //Check if driver has ended the ride
        if(driverFoundID!=null){
            driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
            driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){


                    }else{
                        Toast.makeText(customerMapsActivity.this,  "END DRIVER ID 2" + driverFoundID, Toast.LENGTH_SHORT).show();


                        endRide();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });}
    }

    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    private String getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis() / 1000;

        Date cal = Calendar.getInstance().getTime();
        String date = sdf.format(cal);

        return date;

    }

    private void endRide(){
        Toast.makeText(this, "END DRIVER ID !" + driverFoundID, Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(driverFoundId_s, null);
        editor.putBoolean("req_bol", false);
        editor.apply();


        Status = 0;
        requestBol = false;



        if(driverLocationRef != null){
            driverLocationRef.removeEventListener(driverLocationRefListener);
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);}else{
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }

        if (geoQuery != null)
            geoQuery.removeAllListeners();

        if (driverFoundID != null){

            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(mCustomerMarker != null){
            mCustomerMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        requestRide.setText("FIND MOVER");
        mRadioGroup.setVisibility(View.VISIBLE);
        mBtnLocate.setVisibility(View.VISIBLE);
        mSearchAddress.setVisibility(View.VISIBLE);
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        destinationLatLng = null;




        mDriverProfileImage.setImageResource(R.mipmap.driver);
    }





    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationCallback != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }



    @Override
    public void onBackPressed() {

        if (mNavDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavDrawer.closeDrawer(GravityCompat.START);
        } else {


            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);

                //super.onBackPressed();
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if(driverFoundID==null){

            SharedPreferences.Editor editor1 = sharedpreferences.edit();

            requestBol=false;
            editor1.putBoolean("req_bol", false);

            editor1.apply();

        }


        requestRide.setText("FIND MOVER");
        /*String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);*/
    }

    /*-------------------------------------------- Map specific functions -----
  |  Function(s) onMapReady, showMarker,hideSoftKeyboard,getCurrentLocation
  | gotoLocation,getLocationUpdate
  |
  |  Purpose:  Find and update user's location.
  |
  |  Note:
  |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
  |      If you're having trouble with battery draining too fast then change these to lower values
  |
  |
  *-------------------------------------------------------------------*/

    private void showMarker(double lat, double lng) {
        if (mCustomerMarker != null) {
            mCustomerMarker.remove();
        }
        mCustomerMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("You are Here"));
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                showMarker(location.getLatitude(), location.getLongitude());
                if(mCustomerMarker != null){
                    mCustomerMarker.remove();
                }
                mCustomerMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here"));
                // gotoLocation(location.getLatitude(), location.getLongitude());
                pickupLocation= new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("MAP_DEBUG", "pickup Location " + pickupLocation);


            }

        });


    }
    LocationRequest locationRequest;
    private void getLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());



    }




    private void gotoLocation(double lat, double lng) {

        LatLng latLng = new LatLng(lat, lng);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);

        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP DEBUG", "onMapReady: Map is Showing");

        mGoogleMap = googleMap;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        mGoogleMap.setMyLocationEnabled(true);



    }


    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService
                (INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


  /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult,checkLocationPermission,requestLocationPermission
    | isServicesOk, isGPSEnabled,initGoogleMap
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
  SupportMapFragment supportMapFragment;
    private void initGoogleMap() {

        if (isServicesOk()) {
            if (isGPSEnabled()) {
                if (checkLocationPermission()) {
                    //Toast.makeText(this, "Ready to Map", Toast.LENGTH_SHORT).show();

                   supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map_fragment);

                    supportMapFragment.getMapAsync(this);
                } else {
                    requestLocationPermission();
                }
            }
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            mLocationPermissionGranted = true;
        }
    }

    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(this, "Dialog is cancelled by User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(this, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mLocationPermissionGranted = true;
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else{

                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();




        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {
                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // ********* TOOL BAR ACTIONS********************

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home :
                Intent intent = new Intent(customerMapsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.settings :
                intent = new Intent(customerMapsActivity.this, customerSettingsActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout :
                //if(status ==0)
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(customerMapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.history :
                //if(status ==0)
                intent = new Intent(customerMapsActivity.this, history.class);
                intent.putExtra("customerOrDriver","Customers");
                startActivity(intent);
                finish();
                break;


        }
        return true;
    }
}
