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

public class admin_login_otp extends AppCompatActivity {

    private EditText name;
    private EditText phone;
    private Button login;
    private Button register;
    private Button back;

    private SharedPreferences sp;
    private String sharedPrefFile = "com.ex.mover_f";
    private int logged_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_otp);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
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

            Intent i = new Intent (admin_login_otp.this,admin.class);
            startActivity(i);
            // User is signed in
        } else {

            verify_credentials();
            // No user is signed in
        }
        // [END check_current_user]
    }


    public void verify_credentials(){
        if ((!name.getText().toString().equals(""))  &&  (!phone.getText().toString().equals(""))){

            Intent i = new Intent (admin_login_otp.this, phone_authentication.class);
            i.putExtra("usertype_mobile", "admin$"+phone.getText().toString());
            startActivity(i);
            finish();

        }
    }
}