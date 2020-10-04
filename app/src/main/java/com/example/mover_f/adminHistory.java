package com.example.mover_f;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import historyRecyclerView.HistoryObject;

public class adminHistory extends AppCompatActivity {

    private RecyclerView mAdminHistoryRecyclerView;
    private RecyclerView.Adapter mAdminHistoryAdapter;
    private RecyclerView.LayoutManager mAdminHistoryLayoutManager;
    private String  userId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_history);

        mAdminHistoryRecyclerView = findViewById(R.id.recyclervieww);
        mAdminHistoryRecyclerView.setNestedScrollingEnabled(false);
        mAdminHistoryRecyclerView.setHasFixedSize(true);
        mAdminHistoryLayoutManager = new LinearLayoutManager(this);
        mAdminHistoryRecyclerView.setLayoutManager(mAdminHistoryLayoutManager);
        mAdminHistoryAdapter = new adminHistoryAdapter(getDataSetHistory(), this);
        mAdminHistoryRecyclerView.setAdapter(mAdminHistoryAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserHistoryIds();

    }

    private ArrayList resultsHistory = new ArrayList<adminHistoryObject>();
    private ArrayList<adminHistoryObject> getDataSetHistory() {
        return resultsHistory;
    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins").child(userId).child("myDrivers");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    adminHistoryObject obj;
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        Log.d("Adamx999",history.getValue().toString());
                      obj  = new adminHistoryObject(history.getValue().toString());
                        resultsHistory.add(obj);
                        mAdminHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}