
package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class multipleRideActivity extends AppCompatActivity {

    private String apiKey;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private PlacesClient pickupClient;
    private AutocompleteSupportFragment pickup_autocompleteFragment;
    private String destination = "", pickup ="";
    private LatLng pickupLocation, fragment_pickup , destinationLatLng ;
    public static final String multiple_dest_s = "multiple_dest_s", multiple_loc_s="multiple_loc_s";
    private  String multiple_dest = null, multiple_loc= null;
    public static final String MyPREFERENCES = "com.ex.mover_f";
    SharedPreferences sharedpreferences;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_ride);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        multiple_dest = sharedpreferences.getString(multiple_dest_s, null);
        multiple_loc = sharedpreferences.getString(multiple_loc_s, null);


        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(multiple_dest!=null && multiple_loc!=null){
                Intent intent = new Intent(multipleRideActivity.this, customerMapsActivity.class);
                startActivity(intent);
                finish();}
            }
        });

        apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

       /*** // Create a new Places client instance.
        placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.et_address);
        //  autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS));
        autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.quantum_white_100));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName();
                destinationLatLng = place.getLatLng();
Log.d("ADAMXXX", String.valueOf(place.getAddress()));
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(multiple_dest_s, String.valueOf(destinationLatLng));

                editor1.apply();

                geoLocate();
                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // TODO: Handle the error.


                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });***/
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