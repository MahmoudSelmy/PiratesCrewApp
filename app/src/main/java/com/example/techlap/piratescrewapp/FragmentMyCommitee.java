package com.example.techlap.piratescrewapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyCommitee extends Fragment {

    private RecyclerView membersList;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private String Commitee="";
    private Query commiteeMembersQuery;
    private ArrayList<String> Ids;
    private Bitmap mBitmap;
    private Bundle bundle;
    private UserModel user;

    public FragmentMyCommitee() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_task, container, false);
        mBitmap=null;
        Commitee="";
        Ids=new ArrayList<String>();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        membersList =(RecyclerView)view.findViewById(R.id.membersList);
        membersList.setHasFixedSize(true);
        // to make the list Vertical

        membersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        passCommitee(mAuth.getCurrentUser().getUid());
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        //commiteeMembersQuery = mdatabase.equalTo(Commitee,"commitee");

    }

    public  static class memberViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView imageView;

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
            imageView=(ImageView)mView.findViewById(R.id.memberImage);
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

        }

    }
    private void preRecycler(){
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
                viewHolder.setImage(getActivity().getApplicationContext(),model.getProfileimage(),mBitmap);
                viewHolder.setPosition(model.getPosition());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity(),"To His Profile", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getActivity(),ProfileActivity.class);
                        bundle=new Bundle();
                        bundle.putString("Uid",Ids.get(position));
                        bundle.putInt("Use",0);
                        intent.putExtras(bundle);//Extras not Extra

                        ActivityTransitionLauncher.with(getActivity()).from(view).launch(intent);
                        startActivity(intent);
                    }
                });
            }
        };
        membersList.setAdapter(firebaseRecyclerAdapter);
    }
    private void passCommitee(String Uid) {
        mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(UserModel.class);
                Commitee=user.getCommitee();
                //check if it available offline or not
                if (!Commitee.equals("")){
                    preRecycler();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"Your photo Out",Toast.LENGTH_SHORT).show();
                Log.d("CHECK33","Picasoo Error");
            }
        });
    }

}
