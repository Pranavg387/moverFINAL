package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class customer_login_otp extends AppCompatActivity {


    private EditText name;
    private EditText phone;
    private Button login;
    private Button register;
    private Button back;

    private SharedPreferences sp;
    private String MyPREFERENCES = "com.ex.mover_f";
    private String userType;
    private DatabaseReference db;
    private Boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_otp);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        back = findViewById(R.id.back);

        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userType = sp.getString("userType_s", null);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_credentials();
            }
        });

     checkCurrentUser();





    }

    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null ) {
            db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
            status = false;

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot s : snapshot.getChildren()){

                        if(s.getKey().equals(userId)){
                            status=true;
                            Intent i = new Intent (customer_login_otp.this,customerRides.class);
                            startActivity(i);
                        }

                    }

                    if (status==false){
                        Intent i = new Intent (customer_login_otp.this,LoginActivity.class);
                        startActivity(i);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            // User is signed in
        } else {

            verify_credentials();
            // No user is signed in
        }
        // [END check_current_user]
    }


    public void verify_credentials(){
        if ((!name.getText().toString().equals(""))  &&  (!phone.getText().toString().equals(""))){

                Intent i = new Intent (customer_login_otp.this, phone_authentication.class);
                i.putExtra("usertype_mobile", "customer$"+phone.getText().toString());
                startActivity(i);
                finish();

        }
    }
}