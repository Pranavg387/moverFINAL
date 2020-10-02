package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class driverSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCarField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private String mCar;
    private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;

    private RadioGroup mRadioGroup;
    private RadioButton mSelf ;
    private String empType;
    private EditText mAdminId;
    private FirebaseUser user;
    private DatabaseReference db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mSelf = findViewById(R.id.selfEmployed);
        mAdminId = findViewById(R.id.adminId);
       //mAdminId.setVisibility(View.GONE);

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mCarField = findViewById(R.id.car);

        mProfileImage = findViewById(R.id.profileImage);

        mRadioGroup = findViewById(R.id.radioGroup);

        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

       // mSelf.setVisibility(View.GONE);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(driverSettingsActivity.this, driverMapsActivity.class);
                startActivity(intent1);
                finish();

            }
        });
    }
    private void getUserInfo(){
        DatabaseReference  mDriverDatabase1;
        mDriverDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        mDriverDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("car")!=null){
                        mCar = map.get("car").toString();
                        mCarField.setText(mCar);
                    }
                    if(map.get("adminId")!=null){
                        empType = map.get("adminId").toString();
                        if(empType.equals("SelfEmployed")){
                            mSelf.setChecked(true);
                        }else{
                            mAdminId.setVisibility(View.VISIBLE);
                            mAdminId.setText(empType);
                        }
                    }

                    if(map.get("service")!=null){
                        mService = map.get("service").toString();
                        switch (mService){
                            case"Truck":
                                mRadioGroup.check(R.id.Truck);
                                break;
                            case"Tempo":
                                mRadioGroup.check(R.id.Tempo);
                                break;
                            case"Car":
                                mRadioGroup.check(R.id.Car);
                                break;
                        }
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void saveUserInformation() {
        Log.d("QWA",mSelf.getText().toString());
       if(mSelf.isChecked()){
         mAdminId.setVisibility(View.GONE);
       }


        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCar = mCarField.getText().toString();

        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = findViewById(selectId);

        if (radioButton.getText() == null){
            Toast.makeText(this, "Service Type not Selected", Toast.LENGTH_SHORT).show();
        }


        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mService = radioButton.getText().toString();



        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("car", mCar);
        userInfo.put("service", mService);
        userInfo.put("Status","Offline");

        if(mSelf.isChecked()){
            userInfo.put("adminId", mSelf.getText());
        }else{

            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot s : snapshot.getChildren()){
                        String a = s.child("adminId").getValue().toString();
                        Log.d("adamax0:",a);
                        String key_admin = s.getKey();
                        if (a.equals(mAdminId.getText().toString())){

                            Log.d("adamax1:",userId);
                            DatabaseReference db_new = db.child(key_admin).child("myDrivers").push();
                            db_new.setValue(userId);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userInfo.put("adminId", mAdminId.getText().toString());

        }

        mDriverDatabase.child(userID).setValue(userInfo);

        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                        Intent intent1 = new Intent(driverSettingsActivity.this, driverMapsActivity.class);
                        startActivity(intent1);
                        finish();}
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mDriverDatabase.child(userID).updateChildren(newImage);


                                Intent intent1 = new Intent(driverSettingsActivity.this, driverMapsActivity.class);
                                startActivity(intent1);
                                finish();   }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                                Intent intent1 = new Intent(driverSettingsActivity.this, driverMapsActivity.class);
                                startActivity(intent1);
                                finish(); }
                    });
                }
            });
        }else{

            Intent intent1 = new Intent(driverSettingsActivity.this, driverMapsActivity.class);
            startActivity(intent1);
            finish(); }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    @Override
    protected void onStop() {

        if(mPhoneField.getText().toString().equals("")){

            user.delete();
        }
        super.onStop();

    }

    @Override
    protected void onPause() {

        if(mPhoneField.getText().toString().equals("")){

            user.delete();
        }  super.onPause();
    }

    @Override
    public void onBackPressed() {

        if(mPhoneField.getText().toString().equals("")){

            user.delete();
        }
        super.onBackPressed();
    }
}