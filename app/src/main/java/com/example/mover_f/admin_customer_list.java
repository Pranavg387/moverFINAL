package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import historyRecyclerView.HistoryAdapter;
import historyRecyclerView.HistoryObject;

public class admin_customer_list extends AppCompatActivity {
    private String  userId;

    private RecyclerView mAdminRecyclerView;
    private RecyclerView.Adapter mAdminAdapter;
    private RecyclerView.LayoutManager mAdminManager;

    private ArrayList resultsCustomers = new ArrayList<admin_customer_model>();
    private DatabaseReference db;
    private DatabaseReference db_driver;
    private DatabaseReference db_customer;
    private ArrayList<String> driver_k =  new ArrayList<> ();
    private ArrayList<String> driverHistory =  new ArrayList<> ();
    private ArrayList<String> historyCustomer =  new ArrayList<> ();
    int z ;
    private boolean driver_Status= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer_list);

        mAdminRecyclerView = findViewById(R.id.admin_customers);
        mAdminRecyclerView.setNestedScrollingEnabled(false);
        mAdminRecyclerView.setHasFixedSize(true);
        mAdminManager = new LinearLayoutManager(this);
        mAdminRecyclerView.setLayoutManager(mAdminManager);
        mAdminAdapter = new admin_customer_adap(getDataSetHistory(), this);
        mAdminRecyclerView.setAdapter(mAdminAdapter);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins").child(userId).child("myDrivers");
        db_driver = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        db_customer = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");

        driver_Status = getCustomers();

        findHistory();
        getCustomerInfo();



    }


    private List<admin_customer_model> getDataSetHistory() {

            return resultsCustomers;

    }

    private boolean  getCustomers(){

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                z = (int) snapshot.getChildrenCount();

                for (DataSnapshot s : snapshot.getChildren()){
                    String a = s.getValue().toString();
                    driver_k.add(a);

                }

                findme();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;

    }

    public void findme(){

        for (int i =0; i<driver_k.size();i++){
            DatabaseReference db1 =FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver_k.get(i)).child("history");

            db1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot s : snapshot.getChildren()){

                        driverHistory.add(s.getKey());


                        if(driverHistory.size()==driver_k.size()){
                            findHistory();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }
    }




    public void findHistory(){

        for (int i =0; i<driverHistory.size();i++) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("history").child(driverHistory.get(i));
            int finalI = i;
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    historyCustomer.add((String) snapshot.child("customer").getValue());


                         getCustomerInfo();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    public void getCustomerInfo(){

        for (int i =0; i<historyCustomer.size();i++) {
            DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(historyCustomer.get(i));
            mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        String phone = "", name = "";
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("name") != null) {
                           name= map.get("name").toString();

                        }
                        if (map.get("phone") != null) {

                            phone= map.get("phone").toString();

                        }
                        admin_customer_model cust = new admin_customer_model(name,phone,"");
                        resultsCustomers.add(cust);
                        mAdminAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }



}