package com.example.techlap.piratescrewapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyTasks extends AppCompatActivity {
    private RecyclerView tasksList;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private String name,email,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Tasks");
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Tasks");

        name=getIntent().getExtras().getString("name");
        email=getIntent().getExtras().getString("email");
        image=getIntent().getExtras().getString("image");

        //id=mAuth.getCurrentUser().getUid();
        tasksList = (RecyclerView) findViewById(R.id.recyclerview);
        tasksList.setHasFixedSize(true);
        // to make the list Vertical
        tasksList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference myTasks = mdatabase.child(mAuth.getCurrentUser().getUid());
        myTasks.keepSynced(true);
        Query myTopPostsQuery = myTasks.orderByChild("time");
        myTopPostsQuery.keepSynced(true);
        FirebaseRecyclerAdapter<Task, memberViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, memberViewHolder>(
                Task.class,//PoJo
                R.layout.row_task,//row view
                memberViewHolder.class,//Hoder class
                myTopPostsQuery
        ) {
            @Override
            protected void populateViewHolder(memberViewHolder viewHolder, final Task model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(model.getTime());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(model.getFile()));
                        startActivity(intent);
                    }
                });
            }
        };
        tasksList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(MyTasks.this, Main2Activity.class);
                Bundle bundle=new Bundle();
                bundle.putString("name",name);
                bundle.putString("email",email);
                bundle.putString("image",image);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class memberViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public memberViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String memberName) {
            TextView titleField = (TextView) mView.findViewById(R.id.title);
            titleField.setText(memberName);
        }

        public void setTime(Long time) {
            RelativeTimeTextView descField=(RelativeTimeTextView)mView.findViewById(R.id.description);
            descField.setReferenceTime(time*-1);
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyTasks.this, Main2Activity.class);
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("image",image);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
