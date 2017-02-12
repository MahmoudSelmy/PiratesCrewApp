package com.example.techlap.piratescrewapp;


import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    private RecyclerView postsList;
    private DatabaseReference mdatabase;
    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Posts");
        postsList=(RecyclerView)view.findViewById(R.id.postsList);
        postsList.setHasFixedSize(true);
        // to make the list Vertical

        postsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        //mAuth.addAuthStateListener(mAuthListner);

        Query myTopPostsQuery = mdatabase.orderByChild("time");
        myTopPostsQuery.keepSynced(true);

        FirebaseRecyclerAdapter<PostModel,PostViewHoder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<PostModel, PostViewHoder>(
                PostModel.class,//PoJo
                R.layout.post_design_new_new,//row view
                PostViewHoder.class,//Hoder class
                myTopPostsQuery
        ) {
            @Override
            protected void populateViewHolder(PostViewHoder viewHolder, final PostModel model, final int position) {

                viewHolder.setTitle(model.getTitle());
                Log.d("Titles",model.getTitle());
                //viewHolder.setDesc(model.getDesc());
                //viewHolder.setTime(model.getTime());
                viewHolder.setImage(getActivity().getApplicationContext(),model.getImage());

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Toast.makeText(getActivity(),"you clicked #"+position,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),PostDetailsActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("title",model.getTitle());
                        bundle.putString("desc",model.getDesc());
                        bundle.putString("image",model.getImage());
                        bundle.putLong("time",model.getTime());
                        bundle.putString("key",getRef(position).getKey());
                        intent.putExtras(bundle);
                        ActivityTransitionLauncher.with(getActivity()).from(view).launch(intent);
                        startActivity(intent);
                    }
                });
            }
        };
        postsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHoder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView imageView;

        public PostViewHoder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTitle(String Title){
            TextView titleField=(TextView)mView.findViewById(R.id.post_tittle);
            titleField.setText(Title);
        }

        /*public void setDesc(String Desc){
            TextView descField=(TextView)mView.findViewById(R.id.post_desc);
            descField.setText(Desc);
        }
        public void setTime(long time){
            RelativeTimeTextView descField=(RelativeTimeTextView)mView.findViewById(R.id.timestampPost);
            descField.setReferenceTime(time*-1);
        }*/
        public void setImage(final Context ctx, final String Url){
            imageView=(ImageView)mView.findViewById(R.id.post_image);
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
}
