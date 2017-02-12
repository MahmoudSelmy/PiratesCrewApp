package com.example.techlap.piratescrewapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class CommiteeActivity extends AppCompatActivity {

    private RecyclerView membersList;
    private DatabaseReference mdatabase;
    private Toolbar toolbar;
    private String Commitee="";
    private Query commiteeMembersQuery;
    private ArrayList<String>Ids;
    private Bitmap mBitmap;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitee);

        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBitmap=null;
        Commitee=getIntent().getExtras().getString("Commitee");

        setTitle(Commitee);
        Ids=new ArrayList<String>();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        membersList =(RecyclerView)findViewById(R.id.membersList);
        membersList.setHasFixedSize(true);
        // to make the list Vertical
        membersList.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    protected void onStart() {
        super.onStart();

        //commiteeMembersQuery = mdatabase.equalTo(Commitee,"commitee");
        commiteeMembersQuery = mdatabase.orderByChild("commitee").equalTo(Commitee);//finally done hohoooo
        commiteeMembersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()){
                    String id=child.getKey();
                    Ids.add(id);
                    String name=child.child("name").getValue(String.class);
                    Log.d("CHECK22",id+name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        commiteeMembersQuery.keepSynced(true);

        FirebaseRecyclerAdapter<UserModel,memberViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<UserModel, memberViewHolder>(
                UserModel.class,//PoJo
                R.layout.member_row,//row view
                memberViewHolder.class,//Hoder class
                commiteeMembersQuery
        ) {
            @Override
            protected void populateViewHolder(memberViewHolder viewHolder, final UserModel model, final int position) {

                viewHolder.setName(model.getName());
                viewHolder.setEmail(model.getEmail());
                viewHolder.setImage(getApplicationContext(),model.getProfileimage(),mBitmap);
                viewHolder.setPosition(model.getPosition());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(CommiteeActivity.this,ProfileActivity.class);
                        bundle=new Bundle();
                        bundle.putString("Uid",Ids.get(position));
                        bundle.putInt("Use",1);
                        bundle.putString("Commitee",model.getCommitee());
                        intent.putExtras(bundle);//Extras not Extra
                        startActivity(intent);
                    }
                });
            }
        };
        membersList.setAdapter(firebaseRecyclerAdapter);
    }

    public  static class memberViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        public memberViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String memberName){
            TextView titleField=(TextView)mView.findViewById(R.id.memberName);
            titleField.setText(memberName);
        }

        public void setEmail(String mail){
            TextView descField=(TextView)mView.findViewById(R.id.memberMail);
            descField.setText(mail);
        }
        public void setPosition(String position){
            TextView descField=(TextView)mView.findViewById(R.id.memberPosition);
            descField.setText(position);
        }
        public void setImage(final Context ctx, final String Url,Bitmap bitmapO){
            final ImageView imageView=(ImageView)mView.findViewById(R.id.memberImage);
            //Picasso.with(ctx).load(Url).into(imageView);

            //check if it available offline or not
            Picasso.with(ctx).load(Url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {
                    // if it not available online we need to download it
                    Picasso.with(ctx).load(Url).into(imageView);
                }
            });
            Picasso.with(ctx).load(Url).networkPolicy(NetworkPolicy.OFFLINE).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Picasso.with(ctx).load(Url).into(imageView);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent;
                if (Commitee.equals("Board Of Directors")){
                    intent = new Intent(CommiteeActivity.this, SplashActivity.class);
                }else {
                    intent = new Intent(CommiteeActivity.this, CommiteesActivity.class);

                }

                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (Commitee.equals("Board Of Directors")){
            intent = new Intent(CommiteeActivity.this, SplashActivity.class);
        }else {
            intent = new Intent(CommiteeActivity.this, CommiteesActivity.class);

        }
        startActivity(intent);
        finish();
    }
}
