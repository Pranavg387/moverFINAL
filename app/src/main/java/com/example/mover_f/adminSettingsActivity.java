package com.example.mover_f;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Random;

public class adminSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mAdminDatabase;

    private String userID;
    private String mName;
    private String mPhone;
    private TextView mAdminId;
    private String mProfileImageUrl;

    private Uri resultUri;
    private FirebaseUser user;

    public static final String MyPREFERENCES = "com.ex.mover_f";
    SharedPreferences sharedpreferences;
    private String adminId,adminId_s="adminId_s";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mAdminId = findViewById(R.id.adminId);
        mProfileImage = findViewById(R.id.profileImage);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        adminId = sharedpreferences.getString(adminId_s, null);

       // mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = mAuth.getCurrentUser().getUid();

        userID = mAuth.getCurrentUser().getUid();
        mAdminDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins");

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
              setAdminID();
            }
        });

        /* mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent1 = new Intent(customerSettingsActivity.this, customerMapsActivity.class);
                startActivity(intent1);
                finish();
            }
        });*/

    }

    private  int value= (int)getAdminID();
    private int counter=0;



    private void getUserInfo(){
        DatabaseReference  mCustomerDatabase1;
        mCustomerDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins").child(userID);
        mCustomerDatabase1.addValueEventListener(new ValueEventListener() {
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
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }

                    if(map.get("adminID")!=null){
                        mAdminId.setText( map.get("adminID").toString());

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private long getAdminID(){
        Random rand = new Random();

        int minRange = 100000, maxRange= 500000;
        int value = rand.nextInt(maxRange - minRange) + minRange;
        return value;

    }



    private void setAdminID() {

        DatabaseReference mAdminDatabase1;
        mAdminDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins");


        mAdminDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Log.d("qwazi1", "aac : " + String.valueOf(dataSnapshot.getChildrenCount()));
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Log.d("qwazi1", ds.child("adminId").toString());
                        String checkId = String.valueOf(ds.child("adminId").getValue());
                        Log.d("qwazi1", checkId);
                        String v = String.valueOf(value);
                        Log.d("qwazi2", v);
                        if (checkId.equals(String.valueOf(value))) {
                            Log.d("qwazi3", "HEREIF");
                            counter = counter + 1;
                        }

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (counter == 0) {


                    mName = mNameField.getText().toString();
                    mPhone = mPhoneField.getText().toString();



                    Map userInfo = new HashMap();
                    userInfo.put("name", mName);
                    userInfo.put("phone", mPhone);
                    userInfo.put("adminId", value);
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(adminId_s ,String.valueOf(value));
                    editor1.apply();

                    userInfo.put("myDrivers",true);

                    mAdminDatabase.child(userID).setValue(userInfo);


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
                                Intent intent1 = new Intent(adminSettingsActivity.this, admin.class);
                                startActivity(intent1);
                                finish();
                            }
                        });
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map newImage = new HashMap();
                                        newImage.put("profileImageUrl", uri.toString());
                                        mAdminDatabase.updateChildren(newImage);

                                        Intent intent1 = new Intent(adminSettingsActivity.this, admin.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Intent intent1 = new Intent(adminSettingsActivity.this, admin.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                            }
                        });
                    }else{
                        Intent intent1 = new Intent(adminSettingsActivity.this, admin.class);
                        startActivity(intent1);
                        finish();
                    }

                }
                else {

                    Log.d("adamax : : :","already exists");
                    Toast.makeText(adminSettingsActivity.this, "Please Wait, Do not Exit", Toast.LENGTH_SHORT).show();
                    mConfirm.performClick();
                }
            }
        };
        new Handler().postDelayed(runnable, 5000);
    }


    public interface MyCallback {
        void onCallback(String value);
    }

    private void saveUserInformation() {



        Log.d("adama", "save_user_info: "+String.valueOf(value));


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

        if(String.valueOf(value).equals("")){
            Log.d("XXXB",String.valueOf(value));
            user.delete();
        }
        super.onStop();

    }

    @Override
    protected void onPause() {

        if(String.valueOf(value).equals("")){
            Log.d("XXXB",String.valueOf(value));
            user.delete();
    }  super.onPause();
    }

    @Override
    public void onBackPressed() {



        if(String.valueOf(value).equals("")){
            Log.d("XXXB",String.valueOf(value));
            user.delete();
            }
        super.onBackPressed();
    }
}