package com.example.techlap.piratescrewapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private Bundle bundle;
    private Intent intent;
    private UserModel user;
    private String image="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //startActivity(new Intent(RegisterActivity.this,SetUpActivity.class));

            //startActivity(new Intent(LogInActivity.this,MainActivity.class));
            String Uid=mAuth.getCurrentUser().getUid();
            intent=new Intent(SplashActivity.this,Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            bundle=new Bundle();
            mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user=dataSnapshot.getValue(UserModel.class);
                    image=user.getProfileimage();
                    //profileMini=new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail()).withIcon(user.getProfileimage());
                    if(!image.equals("")){
                        bundle.putString("name",user.getName());
                        bundle.putString("email",user.getEmail());
                        bundle.putString("phone",user.getPhone());
                        bundle.putString("image",image);
                        intent.putExtras(bundle);//Extras not Extra
                        startActivity(intent);
                        finish();
                    }else{
                        //Toast.makeText(LogInActivity.this,"Error Fetching Your Data",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Toast.makeText(LogInActivity.this,"Error Fetching Your Data",Toast.LENGTH_SHORT).show();
                }
            });

            //startActivity(new Intent(LogInActivity.this,Main2Activity.class));

        }else {
            intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
