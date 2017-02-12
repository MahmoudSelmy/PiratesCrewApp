package com.example.techlap.piratescrewapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class CommiteesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VivzAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitees);
        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Commitees");
        recyclerView=(RecyclerView)findViewById(R.id.commiteesList);

        adapter=new VivzAdapter(this,getData(),1);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));// to control the viewing method
    }

    public static List<Information> getData(){
        List<Information> data=new ArrayList<>();

        int[] icons={R.drawable.it,R.drawable.qm,R.drawable.hr,R.drawable.logistics,
                R.drawable.advertising,R.drawable.nontech,R.drawable.fr,R.drawable.pr,
                R.drawable.socialmedial,R.drawable.technical,R.drawable.pm,R.drawable.oa};
        String[]titles={"IT","QM","HR",
                "Logistics","Advertising","Non-Technical",
                "FR","PR","Social Media",
                "Technical","Project Managment","Online Academy"};

        for(int i=0;i<12;i++){
            Information current= new Information();
            current.iconId=icons[i];
            current.title=titles[i];
            data.add(current);
            Log.d("data","piece"+i);
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CommiteesActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(CommiteesActivity.this, SplashActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
