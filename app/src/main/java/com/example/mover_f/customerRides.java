package com.example.mover_f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class customerRides extends AppCompatActivity {

    private String  driverFoundId;

    private RecyclerView mCustomerRideRecyclerView;
    private RecyclerView.Adapter mCustomerRideAdapter;
    private RecyclerView.LayoutManager mCustomerRideManager;

    private ArrayList resultDrivers = new ArrayList<customer_rides_model>();
    private DatabaseReference db;
    public static final String MyPREFERENCES = "com.ex.mover_f";
    SharedPreferences sharedpreferences;
    private String multiple_driver,multiple_driver_s="multiple_driver_s";

    private String[] driver_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_rides);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        multiple_driver = sharedpreferences.getString(multiple_driver_s,null);

        mCustomerRideRecyclerView = findViewById(R.id.customerRidesList);
        mCustomerRideRecyclerView.setNestedScrollingEnabled(false);
        mCustomerRideRecyclerView.setHasFixedSize(true);
        mCustomerRideManager = new LinearLayoutManager(this);
        mCustomerRideRecyclerView.setLayoutManager(mCustomerRideManager);
        mCustomerRideAdapter = new customerRidesAdapter(getDataSetHistory(), this);
        mCustomerRideRecyclerView.setAdapter(mCustomerRideAdapter);
        getDriver();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(customerRides.this, multipleRideActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private List<customer_rides_model> getDataSetHistory() {

        return resultDrivers;

    }


    private void getDriver(){
if(multiple_driver!=null){
        driver_list = multiple_driver.split("\\$");
        Log.d("adamax", driver_list[0]);
        getCustomerInfo();}else{
    getDrivers();
}
    }

    public void getCustomerInfo(){

        for (int i =0; i<driver_list.length;i++) {
            DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver_list[i]).child("customerRequest");
            int finalI = i;
            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        String phone = "", name = "";
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        Log.d("ADMX90909", (String) map.get("destination"));
                        if (map.get("destination") != null) {
                            name= map.get("destination").toString();

                        }
                        if (map.get("destinationLat") != null) {

                            phone= driver_list[finalI];

                        }
                        customer_rides_model cust = new customer_rides_model(name,phone);
                        resultDrivers.add(cust);
                        mCustomerRideAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }



    private DatabaseReference db1;
    int z;
    private ArrayList<String> driver_k =  new ArrayList<> ();


    private boolean  getDrivers(){
        db1 = FirebaseDatabase.getInstance().getReference().child("driversWorking");

        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                z = (int) snapshot.getChildrenCount();

                for (DataSnapshot s : snapshot.getChildren()){
                    String a = s.getKey();
                    driver_k.add(a);
                    Log.d("ADAMXCX",driver_k.toString());

                }
               if(driver_k!=null)
                findme();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;

    }

    private String  userId,iamnew;
    int add=0;

    int q=0;
    public void findme(){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("adamxFIINAL1",userId);

        for (q =0; q<driver_k.size();q++){
            DatabaseReference db1 =FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver_k.get(q)).child("customerRequest").child("customerRideId");
            Log.d("adamxFIINAL2","here");
            SharedPreferences.Editor editor = sharedpreferences.edit();

            int finalI = q;
            db1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()&& snapshot.getValue().toString().equals(userId)){

                            Log.d("adamxFINAL",snapshot.getValue().toString());
                            Log.d("adamxFINA2L",driver_k.get(finalI));

                            if(multiple_driver ==null){

                                multiple_driver=driver_k.get(finalI);
                                Log.d("ADAMX909",multiple_driver);
                            }else {
                                multiple_driver += ("$" + driver_k.get(finalI));
                                Log.d("ADAMX9091",multiple_driver);

                            }

                            editor.putString(multiple_driver_s,multiple_driver);

                            editor.apply();


                        }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }if((q+1)==driver_k.size()){
            getDriver();
        }
    }
}