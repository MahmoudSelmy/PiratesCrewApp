package com.example.techlap.piratescrewapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PostDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String name, email, title, desc, postimg,key;
    private Long time;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase,mdatabasePosts;
    private UserModel user;
    private String image = "";
    private RelativeTimeTextView timeView;
    private TextView titleView, descView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        ActivityTransition.with(getIntent()).to(findViewById(R.id.postImage)).start(savedInstanceState);


        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabasePosts = FirebaseDatabase.getInstance().getReference().child("Posts");

        mdatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Details");
        title = getIntent().getExtras().getString("title");
        desc = getIntent().getExtras().getString("desc");
        postimg = getIntent().getExtras().getString("image");
        time = getIntent().getExtras().getLong("time");
        key = getIntent().getExtras().getString("key");

        String Uid = mAuth.getCurrentUser().getUid();

        timeView = (RelativeTimeTextView) findViewById(R.id.postTime);
        titleView = (TextView) findViewById(R.id.postTitle);
        descView = (TextView) findViewById(R.id.postDesc);
        imageView = (ImageView) findViewById(R.id.postImage);


        timeView.setReferenceTime(-1 * time);
        titleView.setText(title);
        descView.setText(desc);
        Picasso.with(this).load(postimg).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                // if it not available online we need to download it
                Picasso.with(getApplicationContext()).load(postimg).into(imageView);
            }
        });

        mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserModel.class);
                image = user.getProfileimage();
                name = user.getName();
                email = user.getEmail();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostDetailsActivity.this, Main2Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("image", image);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(PostDetailsActivity.this, Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("image", image);
                intent.putExtras(bundle);
                ActivityTransitionLauncher.with(PostDetailsActivity.this).from(imageView).launch(intent);
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this).setMessage("Delete?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(checkInternetConenction(PostDetailsActivity.this)){
                                    mdatabasePosts.child(key).removeValue();
                                    startActivity(new Intent(PostDetailsActivity.this,SplashActivity.class));
                                }else{
                                    Toast.makeText(PostDetailsActivity.this,"Check Intenet Connection",Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private Boolean checkInternetConenction(Context context) {
        Boolean internet_status = false;
        ConnectivityManager check = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null) {
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        internet_status = true;
                    }
                }
        }
        return internet_status;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

}
