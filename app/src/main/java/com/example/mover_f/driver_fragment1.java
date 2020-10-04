package com.example.mover_f;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class driver_fragment1 extends Fragment {
    private RecyclerView recyclerView;
    View v;
    ProgressDialog progressDialog;
    private SharedPreferences sp;
    private String MyPREFERENCES = "com.ex.mover_f";
    private String adminId;

    private List<driver_model> data = new ArrayList<>();

    RandomNumListAdapter rr;
    DatabaseReference db;

    @Override
    public View onCreateView(

            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        sp = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        adminId = sp.getString("adminId_s", null);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.driver_fragment1_layout, container, false);
        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        db.keepSynced(true);



        /**

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new RandomNumListAdapter(1234));

        return view;**/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;
        init();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Syncing");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadData();

    }

    
    private void loadData(){

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    if(s.hasChild("Status") && s.child("Status").getValue().toString().equals("Available") && s.child("adminId").getValue().toString().equals(adminId)){

                        data.add(s.getValue(driver_model.class));}

                }


                rr.notifyDataSetChanged();
                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void init(){
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        rr = new RandomNumListAdapter(data,getActivity());
        recyclerView.setAdapter(rr);
        rr.notifyDataSetChanged();


    }
}
