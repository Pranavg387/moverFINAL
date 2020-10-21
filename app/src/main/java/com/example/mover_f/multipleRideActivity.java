
package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class multipleRideActivity extends AppCompatActivity {

    private String apiKey;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private PlacesClient pickupClient;
    private AutocompleteSupportFragment pickup_autocompleteFragment;
    private String destination = "", pickup ="";
    private Boolean requestBol = false;
    private FusedLocationProviderClient mLocationClient;

    private LatLng pickupLocation, fragment_pickup , destinationLatLng ;
    public static final String multiple_dest_s = "multiple_dest_s", multiple_loc_s="multiple_loc_s";
    private  String multiple_dest = null, multiple_loc= null;
    public static final String MyPREFERENCES = "com.ex.mover_f";
    SharedPreferences sharedpreferences;
    private Button done;
    public static final String rec_drive_can_s="rec_drive_can_s";
    private String rec_driv_can = null;
    private String driverFoundID = null;
    public static final String driverFoundId_s = "driverFoundId_s";
    private RadioGroup mRadioGroup;
    String requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_ride);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        rec_driv_can = sharedpreferences.getString(rec_drive_can_s,null);

        requestBol = sharedpreferences.getBoolean("req_bol",false);

        multiple_dest = sharedpreferences.getString(multiple_dest_s, null);
        multiple_loc = sharedpreferences.getString(multiple_loc_s, null);

        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.Truck);

        done = findViewById(R.id.done);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiple_dest!=null && multiple_loc!=null){
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

                    if (ActivityCompat.checkSelfPermission(multipleRideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(multipleRideActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mLocationClient.getLastLocation().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();

                            pickupLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("MAP_DEBUG", "pickup Location " + pickupLocation);

                            getCurrentLocation();
                            // getLocationUpdate();

                            //Send to Database
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

                            GeoFire geoFire = new GeoFire(ref);

                            if(fragment_pickup==(null))
                                geoFire.setLocation(userId, new GeoLocation(pickupLocation.latitude, pickupLocation.longitude));
                            else{
                                pickupLocation = fragment_pickup;
                                geoFire.setLocation(userId, new GeoLocation(pickupLocation.latitude, pickupLocation.longitude));

                            }
                            //LOOK FOR DRIVERS
                            getClosestDriver();


                        }
                    });

                  }
            }
        });

        apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }


        placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.et_address);
        //  autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.quantum_white_100));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName();
                destinationLatLng = place.getLatLng();
                String nass = new String((place.getLatLng().latitude)+","+place.getLatLng().longitude);
                Log.d("ADAMXXX", String.valueOf(place.getLatLng()));
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(multiple_dest_s, String.valueOf(nass));
                editor1.apply();
                Log.d("ADAMX999", String.valueOf(nass));

                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // TODO: Handle the error.


                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        pickupClient = Places.createClient(this);
        pickup_autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.et_pickup);
        //  autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        pickup_autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        pickup_autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.quantum_white_100));

        pickup_autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                fragment_pickup = place.getLatLng();
                Log.d("ADAMXXX", String.valueOf(fragment_pickup));

                pickup = new String((place.getLatLng().latitude)+","+place.getLatLng().longitude);

                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(multiple_loc_s, String.valueOf(pickup));
                editor1.apply();

                Log.d("ADAMX999", String.valueOf(pickup));
                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // TODO: Handle the error.


                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private int radius = 1;
    private Boolean driverFound = false;

    private GeoQuery geoQuery;
    //shared preferences

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




                                if (driverMap.get("service").equals(requestService) ) {
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    Toast.makeText(multipleRideActivity.this, "id going in sp :" + driverFoundID, Toast.LENGTH_SHORT).show();
                                    editor.putString(driverFoundId_s, driverFoundID);
                                    editor.apply();

                                    Intent intent = new Intent(multipleRideActivity.this, customerMapsActivity.class);
                                    intent.putExtra("driverFoundId", driverFoundID);
                                    startActivity(intent);
                                    finish();






                                   /*** SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                    editor1.putString(rec_drive_can_s, null);
                                    editor1.apply();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    Toast.makeText(multipleRideActivity.this, "id going in sp :" + driverFoundID, Toast.LENGTH_SHORT).show();
                                    editor.putString(driverFoundId_s, driverFoundID);
                                    editor.apply();***/


                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                   // if (destinationLatLng != null) {
                                        map.put("destinationLat", destinationLatLng.latitude);
                                        map.put("destinationLng", destinationLatLng.longitude);
                                        driverRef.updateChildren(map);
                                  //  } else {
                                     //   Toast.makeText(multipleRideActivity.this, "Choose a Destination", Toast.LENGTH_SHORT).show();
                                   // }





                                    getDriverLocation();
                                    //getDriverInfo();
                                    //getHasRideEnded();
                                    //requestRide.setText("Looking for Driver Location....");

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
                        //   requestRide.setText("Driver Found");

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


                        Location loc1 = new Location("");
                        loc1.setLatitude(pickupLocation.latitude);
                        loc1.setLongitude(pickupLocation.longitude);

                        Location loc2 = new Location("");
                        loc2.setLatitude(driverLatLng.latitude);
                        loc2.setLongitude(driverLatLng.longitude);

                        float distance = loc1.distanceTo(loc2);

                        if (distance<100){
                            // requestRide.setText("Driver is Here");
                        }else{
                            // requestRide.setText("Driver Found" + " "+ distance +"m Away");
                        }




                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });}


    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
               // gotoLocation(location.getLatitude(), location.getLongitude());
                pickupLocation= new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("MAP_DEBUG", "pickup Location " + pickupLocation);


            }

        });


    }

    private void geoLocate() {





        // destination = mSearchAddress.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(destination, 3);

            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());


                /***showMarker
                  gotoLocation(address.getLatitude(), address.getLongitude());
                if(mDestinationMarker != null){
                    mDestinationMarker.remove();
                 mDestinationMarker =mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Destination"));
                 Toast.makeText(this, address.getLocality(), Toast.LENGTH_SHORT).show();
                }***/


                Log.d("MAP_DEBUG", "geoLocate: Locality: " + address.getLocality());
            }

            for (Address address : addressList) {
                Log.d("MAP_DEBUG", "geoLocate: Address: " + address.getAddressLine(address.getMaxAddressLineIndex()));
            }


        } catch (IOException e) {


        }


    }
}