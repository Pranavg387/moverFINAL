package com.example.mover_f;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class driver_login_otp extends AppCompatActivity {
    private EditText name;
    private EditText phone;
    private Button login;

    private Button back;

    private SharedPreferences sp;
    private String sharedPrefFile = "com.ex.mover_f";
    private int logged_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_otp);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);

        back = findViewById(R.id.back);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_credentials();
            }
        });



        checkCurrentUser();


    }



    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Intent i = new Intent(driver_login_otp.this, driverMapsActivity.class);
            startActivity(i);
            // User is signed in
        } else {

            verify_credentials();
            // No user is signed in
        }
        // [END check_current_user]
    }


    public void verify_credentials() {
        if ((!name.getText().toString().equals("")) && (!phone.getText().toString().equals(""))) {

            Intent i = new Intent(driver_login_otp.this, phone_authentication.class);
            i.putExtra("usertype_mobile", "driver$" + phone.getText().toString());
            startActivity(i);
            finish();

        }
    }
}