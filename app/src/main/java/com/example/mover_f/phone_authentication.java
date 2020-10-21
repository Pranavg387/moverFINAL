package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class phone_authentication extends AppCompatActivity {

    private EditText otp;
    private Button verify;
    private Button resent;

    private FirebaseAuth mAuth;
    private String mVerififcationId;
    private String contact;
    private String user_type;
    private SharedPreferences sp;
    private String sharedprefFile = "com.ex.mover.f";
    private DatabaseReference databaseUsers;
    private DatabaseReference mDriverDatabase;


    private int counter=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference();
        otp = findViewById(R.id.otp);
        verify = findViewById(R.id.verify);
        resent = findViewById(R.id.resend);
        Intent intent = getIntent();
        String raw_data = intent.getStringExtra("usertype_mobile");
        user_type = raw_data.substring(0, raw_data.indexOf("$"));
        contact = raw_data.substring(raw_data.indexOf("$") + 1, raw_data.length());

        sendVerificatationCode(contact);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = otp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(phone_authentication.this, "Invalid Code", Toast.LENGTH_LONG).show();
                }

                verifyVerificationCode(code);
            }
        });
    }

    private void sendVerificatationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+no, 60 /*timeout*/, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,
                mCallbacks);

    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            super.onCodeSent(verificationId, forceResendingToken);
            mVerififcationId = verificationId;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code!=null){
                otp.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(phone_authentication.this, "Authentication Barbad" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    };




    private void verifyVerificationCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerififcationId,code);
        signInWithCredential(credential);
    }


    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            if (user_type.equals("driver"))
                            {

                                String user_id = mAuth.getCurrentUser().getUid();
                                mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);

                                mDriverDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                            Log.d("adam",dataSnapshot.child("phone").toString());

                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                            if(map.get("phone").equals(contact)){
                                                Intent i = new Intent (phone_authentication.this, driverMapsActivity.class);
                                                startActivity(i);
                                                finish();

                                            }else{
                                                String userID;
                                                userID = mAuth.getCurrentUser().getUid();
                                                Intent i = new Intent (phone_authentication.this, driverSettingsActivity.class);
                                                startActivity(i);
                                                finish();

                                            }

                                        }else{
                                            String userID;
                                            userID = mAuth.getCurrentUser().getUid();
                                            Intent i = new Intent (phone_authentication.this, driverSettingsActivity.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if (user_type.equals("admin")){
                                String user_id = mAuth.getCurrentUser().getUid();
                                mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins").child(user_id);
                                Log.d("XXX", String.valueOf(mDriverDatabase));
                                mDriverDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d("XXX2", String.valueOf(dataSnapshot));
                                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                            Log.d("adam",dataSnapshot.child("phone").toString());

                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                            if(map.get("phone").equals(contact)){
                                                Intent i = new Intent (phone_authentication.this, admin.class);
                                                startActivity(i);
                                                finish();

                                            }else{
                                                String userID;
                                                userID = mAuth.getCurrentUser().getUid();
                                                Intent i = new Intent (phone_authentication.this, adminSettingsActivity.class);
                                                startActivity(i);
                                                finish();

                                            }

                                        }else{
                                            String userID;
                                            userID = mAuth.getCurrentUser().getUid();
                                            Intent i = new Intent (phone_authentication.this, adminSettingsActivity.class);
                                            startActivity(i);
                                            finish();}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                            else
                            {
                                String user_id = mAuth.getCurrentUser().getUid();
                                mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);

                                mDriverDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                            Log.d("adam",dataSnapshot.child("phone").toString());

                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                            if(map.get("phone").equals(contact)){
                                                Intent i = new Intent (phone_authentication.this, customerRides.class);
                                                startActivity(i);
                                                finish();

                                            }else{
                                                String userID;
                                                userID = mAuth.getCurrentUser().getUid();
                                                Intent i = new Intent (phone_authentication.this, customerSettingsActivity.class);
                                                startActivity(i);
                                                finish();

                                            }

                                        }else{
                                            String userID;
                                            userID = mAuth.getCurrentUser().getUid();
                                            Intent i = new Intent (phone_authentication.this, customerSettingsActivity.class);
                                            startActivity(i);
                                            finish();}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


                        } else {

                            Toast.makeText(phone_authentication.this, "AUTH FAILED", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

}