package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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



    }


    private List<admin_customer_model> getDataSetHistory() {

            return resultsCustomers;

    }

    private boolean  getCustomers(){
        Log.d("adamasz", String.valueOf(z));

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("adamasz", String.valueOf(snapshot.getChildrenCount()));
                z = (int) snapshot.getChildrenCount();

                for (DataSnapshot s : snapshot.getChildren()){
                    String a = s.getValue().toString();

                    //Log.d("adamax0:",a);
                    driver_k.add(a);

                }

                findme();
                Log.d("adamax1:",driver_k.toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;

    }

    public void findme(){
        Log.d("adamax200", String.valueOf(driver_k.size()));
        for (int i =0; i<driver_k.size();i++){
            Log.d("adamasz1", String.valueOf(i));
            DatabaseReference db1 =FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver_k.get(i)).child("history");

            db1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     Log.d("adamax3", String.valueOf("starting inner loop"));

                    for (DataSnapshot s : snapshot.getChildren()){

                        Log.d("adamax3", String.valueOf(s.getKey()));//if  deriver_k
                        //if (s.getKey())
                        driverHistory.add(s.getKey());


                        if(driverHistory.size()==driver_k.size()){
                            findHistory();
                        }

                    }

                    Log.d("adamax222:", String.valueOf(driverHistory));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }
    }




    public void findHistory(){

        Log.d("adamax2001", String.valueOf(driverHistory));
        for (int i =0; i<driverHistory.size();i++) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("history").child(driverHistory.get(i));
            int finalI = i;
            Log.d("adamax43", String.valueOf(db.getKey()));//if  deriver_k
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("adamax75", String.valueOf(snapshot.child("customer").getValue()));
                    historyCustomer.add((String) snapshot.child("customer").getValue());
                    Log.d("adamax78", String.valueOf(historyCustomer));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}