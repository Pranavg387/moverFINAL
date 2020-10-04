package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mNavDrawer;
    private SharedPreferences sp;
    private String MyPREFERENCES = "com.ex.mover_f";
    private String userType,adminId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout_a);

        Toolbar toolbar_d = findViewById(R.id.toolbar_a);
        setSupportActionBar(toolbar_d);
        getSupportActionBar().setTitle(null);

        sp =  getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        adminId = sp.getString("adminId_s",null);
        mNavDrawer = findViewById(R.id.drawer_layout_a);
        NavigationView navigationView = findViewById(R.id.navigation_view_a);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, toolbar_d, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins").child(userId);
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    SharedPreferences.Editor editor1 = sp.edit();
                    editor1.putString("adminId_s" ,String.valueOf(snapshot.child("adminId").getValue()));

                editor1.apply();
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Driver:
                Intent intent = new Intent(this, admin_driver_stat.class);
                startActivity(intent);
                finish();
                break;

            case R.id.Customer:
                intent = new Intent(this, admin_customer_list.class);
                startActivity(intent);
                finish();
                break;

            case R.id.history :
                //if(status ==0)
               intent = new Intent(this, adminHistory.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout :
                //if(status ==0)
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;


        }
        return true;
    }


}
