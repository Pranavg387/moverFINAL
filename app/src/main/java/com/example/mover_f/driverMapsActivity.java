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
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.mover_f.customerMapsActivity.driverFoundId_s;

public class driverMapsActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener, NavigationView.OnNavigationItemSelectedListener {


    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int GPS_REQUEST_CODE = 9003;
    boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private Button mBtnLocate, mSettings, mRideStatus;
    //  private EditText mSearchAddress;
    //  private Button mCurrent_Location;
    public static final int DEFAULT_ZOOM = 15;
    private final double ISLAMABAD_LAT = 33.690904;
    private final double ISLAMABAD_LNG = 73.051865;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    private LatLng mDestination;

    private Marker mDriverMarker = null;
    private Marker mPickupLocation = null;
    private Marker mDestinationMarker = null;
    private long backPressedTime;

    private float rideDistance;

    public static final String TAG = "MapDebug";
    private Toast backToast;
    LatLng pickUpLatLng;
    private LatLng destinationLatLng;

    Location mLastLocation;
    private String customerId = "", destination;
    private LinearLayout mCustomerInfo;

    private int status = 0;
    private ImageView mCustomerProfileImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;

    private DrawerLayout mNavDrawer;

    private Switch mWorkingSwitch;

    //Send to Database
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference refAvailable;
    DatabaseReference refWorking;
    GeoFire geoFireAvailable;
    GeoFire geoFireWorking;


    //
    DatabaseReference driverStat = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //***************TOOLBAR**********************

        setContentView(R.layout.nav_drawe_layout_d);
        Toolbar toolbar_d = findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar_d);
        getSupportActionBar().setTitle(null);

        destinationLatLng = new LatLng(0.0, 0.0);
//        destinationLatLng.
        mNavDrawer = findViewById(R.id.drawer_layout_d);
        NavigationView navigationView = findViewById(R.id.navigation_view_d);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, toolbar_d, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //*************************************
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        customerId = sharedpreferences.getString(customerId_s, "");
        status = sharedpreferences.getInt("status_s", 0);

        polylines = new ArrayList<>();

        mCustomerInfo = findViewById(R.id.customerInfo);

        mCustomerProfileImage = findViewById(R.id.customerProfileImage);

        mCustomerName = findViewById(R.id.customerName);
        mCustomerPhone = findViewById(R.id.customerPhone);
        mCustomerDestination = findViewById(R.id.customerDestination);



        mWorkingSwitch = findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //getCurrentLocation();
                    getLocationUpdate();

                    if (ActivityCompat.checkSelfPermission(driverMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(driverMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mGoogleMap.setMyLocationEnabled(true);

                   refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                     refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    geoFireAvailable = new GeoFire(refAvailable);
                     geoFireWorking = new GeoFire(refWorking);


                } else {
                    disconnectDriver();
                }
            }
        });


        // mSearchAddress = findViewById(R.id.et_address);
        //mBtnLocate = findViewById(R.id.btn_locate);


        initGoogleMap();
        mLocationClient = new FusedLocationProviderClient(this);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        // mBtnLocate.setOnClickListener(this::geoLocate);
       Log.d("adama", String.valueOf(status));




        mRideStatus = findViewById(R.id.rideStatus);
        Log.d("adama",mRideStatus.getText().toString());
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 1:
                        status = 2;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("status_s", status);
                        editor.apply();


                        erasePolylines();
                        if (destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0) {
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("drive completed");

                        break;
                    case 2:
                        recordRide();
                        endRide();

                        break;
                }
            }
        });



      /*  mCurrent_Location = findViewById(R.id.current_location);
        mCurrent_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrent_Location.setText("ON DUTY");
                getCurrentLocation();
                getLocationUpdate();
            }
        });*/

        mLocationClient = new FusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    if(getApplicationContext()!=null) {

                        if (!customerId.equals("") && mLastLocation != null && location != null) {
                            rideDistance += mLastLocation.distanceTo(location) / 1000;

                            //getAssignedCustomer();
                        }
                        mLastLocation = location;
                        //Toast.makeText(DriverMapsActivity.this, "LocationResult: Location is " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        Log.d("MAP_DEBUG", "onLocationResult: Location is " + location.getLatitude() + " ," + location.getLongitude());

                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    if(geoFireWorking!=null && geoFireAvailable!=null){

                        switch (customerId) {
                            case "":

                                geoFireWorking.removeLocation(userId);
                               driverStat.child("Status").setValue("Available");
                                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                                break;

                            default:

                                geoFireAvailable.removeLocation(userId);

                                driverStat.child("Status").setValue("Working");
                                geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                                break;


                        }}

                    }}
            }
        };

        //getAssignedCustomer();
        if(status==2 ){

            mRideStatus.setText("drive completed");

        }


    }

    //shared preferences
    public static final String MyPREFERENCES = "com.ex.mover_f";

    public static final String customerId_s = "";
    SharedPreferences sharedpreferences;


    private void getAssignedCustomer() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   if(status!=2){
                    status = 1;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("status_s", status);

                    customerId = dataSnapshot.getValue().toString();
                    editor.putString(customerId_s, customerId);
                    editor.apply();}



                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

     /*-------------------------------------------- Map specific functions -----
    |  Function(s) getCustomerLocation,getCustomerDestination
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

    DatabaseReference assignedCustomerPickupLocationRef;
    ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerPickupLocation() {

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("pickLocation").child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !customerId.equals("")) {

                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }

                    pickUpLatLng = new LatLng(locationLat, locationLng);

                    if (mPickupLocation != null) {
                        mPickupLocation.remove();
                    }

                    Toast.makeText(driverMapsActivity.this, "lat" + locationLat, Toast.LENGTH_SHORT).show();
                    mPickupLocation = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(locationLat, locationLng)).title("pickup location"));

                    if (status == 1){
                        Log.d("adama",String.valueOf(pickUpLatLng.latitude));
                        getRouteToMarker(pickUpLatLng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("destination") != null) {
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    } else {
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if (map.get("destinationLat") != null) {
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if (map.get("destinationLng") != null) {
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);

                        if (mDestinationMarker != null) {
                            mDestinationMarker.remove();
                        }
                        mDestinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

                        if (status == 2){
                            getRouteToMarker(destinationLatLng);}
                    }

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /*-------------------------------------------- getCustomerInfo -----
    |  Function(s) getCustomerInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getAssignedCustomerInfo() {

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCustomerInfo.setVisibility(View.VISIBLE);
                mRideStatus.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mCustomerName.setText("Name:" + map.get("name").toString());
                    }
                    if (map.get("phone") != null) {

                        mCustomerPhone.setText("Contact :" + map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


 /*-------------------------------------------- WORKING FUNCTIONALITY-----
    |  Function(s) getRouteToMarker,endRide,onBackPressed, *OnPause*
    | geoLocate,onStop
    |
    |  Purpose:  Help App operate and perform functions
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/


    private void getRouteToMarker(LatLng mLatLng) {
        Log.d("adama",String.valueOf(mLatLng));
        Log.d("adama",String.valueOf(mLastLocation));

        if (mLatLng != null && mLastLocation != null) {
            Log.d("adama","ROUTING");
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .key("AIzaSyBO79iYODZlqN51cCGdWl-eoKsiz-kPUTI")
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), mLatLng)
                    .build();
            routing.execute();
        }
    }


    private void recordRide() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        map.put("location/from/lat", pickUpLatLng.latitude);
        map.put("location/from/lng", pickUpLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis() / 1000;
        return timestamp;
    }

    private void endRide() {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(customerId_s, "");




        mRideStatus.setText("picked customer");
        erasePolylines();
        status = 0;
        editor.putInt("status_s", status);
        editor.apply();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId = "";
        rideDistance = 0;
        if (mPickupLocation != null) {
            mPickupLocation.remove();
        }
        if (mDriverMarker != null) {
            mDriverMarker.remove();
        }

        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null) {
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.profileimage_round);
        mRideStatus.setVisibility(View.GONE);
        // mCurrent_Location.setText("OFF DUTY");


    }


    private void disconnectDriver() {
        if (mLocationClient != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        driverStat.child("Status").setValue("Offline");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (mDriverMarker != null) {
            mDriverMarker.remove();
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

   /* @Override
    protected void onPause() {
        super.onPause();
        if (mLocationCallback != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }*/

   /*
   @Override
    protected void onStop() {
        super.onStop();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }
  */





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
        if (mDriverMarker != null) {
            mDriverMarker.remove();
        }
        mDriverMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("You are Here"));

    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService
                (INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is showing on the screen");

        mGoogleMap = googleMap;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);

        getAssignedCustomer();

    }

    private void gotoLocation(double lat, double lng) {

        LatLng latLng = new LatLng(lat, lng);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);

        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }


    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            } else {
                Log.d(TAG, "getCurrentLocation: Error: " + task.getException().getMessage());
            }

        });

    }
    LocationRequest locationRequest;
    private void getLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());




    }

    /*-------------------------------------------- onRequestPermissionsResult -------------
    |  Function onRequestPermissionsResult,checkLocationPermission,requestLocationPermission
    | isServicesOk, isGPSEnabled,initGoogleMap
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *---------------------------------------------------------------------------------------*/
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

    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
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



    //Direction API

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    // ********* TOOL BAR ACTIONS********************
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home :
                if(status==0){
                    Intent intent = new Intent(driverMapsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;}else {
                    Toast.makeText(this, "Cannot Open During a Ride", Toast.LENGTH_SHORT).show();
                }

            case R.id.settings :
                if(status==0){
                    Intent intent1 = new Intent(driverMapsActivity.this, driverSettingsActivity.class);
                    startActivity(intent1);
                    finish();
                    break;}else {
                    Toast.makeText(this, "Cannot Open During a Ride", Toast.LENGTH_SHORT).show();
                }

            case R.id.logout :
                if(status ==0){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(driverMapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;}else {
                    Toast.makeText(this, "Cannot Open During a Ride", Toast.LENGTH_SHORT).show();}

            case R.id.history :
                if(status==0){
                    Intent intent1 = new Intent(driverMapsActivity.this, history.class);
                    intent1.putExtra("customerOrDriver","Drivers");
                    startActivity(intent1);
                    finish();
                    break;}else {
                    Toast.makeText(this, "Cannot Open During a Ride", Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }
}