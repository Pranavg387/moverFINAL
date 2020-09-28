package com.example.mover_f;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button mDriver, mCustomer,mAdmin;
    public static final String MyPREFERENCES = "com.ex.mover_f";
    SharedPreferences sharedpreferences;

    private String userType,userType_s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mDriver = findViewById(R.id.driver);
        mCustomer= findViewById(R.id.customer);
        mAdmin = findViewById(R.id.admin);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userType = sharedpreferences.getString(userType_s, null);

        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(userType_s ,"Driver");
                editor1.apply();

                Intent intent = new Intent(LoginActivity.this, driver_login_otp.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(userType_s ,"Customer");
                editor1.apply();

                Intent intent = new Intent(LoginActivity.this, customer_login_otp.class);
                startActivity(intent);
                finish();
                return;

            }
        });

     mAdmin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(userType_s ,"Admin");
            editor1.apply();

            Intent intent = new Intent(LoginActivity.this, admin_login_otp.class);
            startActivity(intent);
            finish();
            return;

        }
    });
}

}
