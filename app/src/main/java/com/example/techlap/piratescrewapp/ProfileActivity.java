
package com.example.techlap.piratescrewapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kogitune.activity_transition.ActivityTransition;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    //private Toolbar toolbar;
    private TextView phone,mail, position,personalInfo;
    private UserModel user;
    private String Uid,commitee;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private ImageView pic;
    private int Use;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setTitle("Profile");
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mail=(TextView)findViewById(R.id.profileEmail);
        position=(TextView)findViewById(R.id.profilePosition);
        personalInfo=(TextView)findViewById(R.id.profilePersonalInfo);
        phone=(TextView)findViewById(R.id.profilePhone);
        pic=(ImageView)findViewById(R.id.profile_id);

        ActivityTransition.with(getIntent()).to(pic).start(savedInstanceState);

        Uid=getIntent().getExtras().getString("Uid");
        Use=getIntent().getExtras().getInt("Use");
        if(Use!=0){
            commitee=getIntent().getExtras().getString("Commitee");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ToDo: don't use == use equals
        if (Uid.equals("myProfile")){
            //Toast.makeText(ProfileActivity.this,"in If",Toast.LENGTH_LONG).show();
            passData(mAuth.getCurrentUser().getUid());
        }else{
            passData(Uid);
        }
        mdatabase.keepSynced(true);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        toolbarTextAppernce();

    }

    private void passData(String Uid) {
        mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user=dataSnapshot.getValue(UserModel.class);
                collapsingToolbarLayout.setTitle(user.getName());
                position.setText(user.getPosition());
                personalInfo.setText(user.getPersonalInfo());
                mail.setText(user.getEmail());
                phone.setText(user.getPhone());
                Picasso.with(getApplicationContext()).load(user.getProfileimage()).networkPolicy(NetworkPolicy.OFFLINE).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        // if it not available online we need to download it
                        Picasso.with(getApplicationContext()).load(user.getProfileimage()).into(pic);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"Error Fetching Your Data",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(Use==0){
            Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(ProfileActivity.this,CommiteeActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("Commitee",commitee);
            intent.putExtras(bundle);//Extras not Extra
            startActivity(intent);
        }

    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }
}
