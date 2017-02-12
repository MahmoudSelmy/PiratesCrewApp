package com.example.techlap.piratescrewapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class VivzAdapter extends RecyclerView.Adapter<VivzAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<Information> data= Collections.emptyList();// the null of List
    private Context context;
    private int use;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private Intent intent;
    private Bundle bundle;
    private String commitee="";
    private UserModel user;
    private String  image="";

    public VivzAdapter(Context context,List<Information> data,int use){

        inflater=LayoutInflater.from(context);
        this.data=data;
        this.context=context;
        this.use=use;
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

    }

    // make the holder have a ref of the row layout
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //get ref of the layout
        View view=null;
        if (use==0){
            view=inflater.inflate(R.layout.custom_row,parent,false);

        }else if (use==1){
            view=inflater.inflate(R.layout.commitee_row,parent,false);
        }
        MyViewHolder holder=new MyViewHolder(view);
        Log.d("onCreateHolder","Piece");
        return holder;
    }

    //populate the holder with data
    // we can here add a oncilck Listner
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Information current=data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void delete(int postion){
        data.remove(postion);
        notifyItemRemoved(postion);
    }


    //Listners here are more accurate than onBlindView
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView title;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);

            Log.d("Holder","Piece "+data.get(2).title);
            title= (TextView) itemView.findViewById(R.id.listText);
            icon=  (ImageView) itemView.findViewById(R.id.listIcon);
            title.setOnClickListener(this);
            title.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

          if (use==1){
                String Commitee="";
                switch (getLayoutPosition()){
                    case 0: //profile case
                        Commitee="IT";
                        break;
                    case 1://Committes
                        Commitee="QM";
                        break;
                    case 2:
                        Commitee="HR";
                        break;
                    case 3: //profile case
                        Commitee="Logistics";
                        break;
                    case 4://Committes
                        Commitee="Advertising";
                        break;
                    case 5:
                        Commitee="Non-Technical Track";
                        break;
                    case 6: //profile case
                        Commitee="FR";
                        break;
                    case 7://Committes
                        Commitee="PR";
                        break;
                    case 8:
                        Commitee="Social Media";
                        break;
                    case 9: //profile case
                        Commitee="Technical Track";
                        break;

                    case 10: //profile case
                        Commitee="Project Management";
                        break;
                    case 11: //profile case
                        Commitee="Online Academy";
                        break;

                }
                intent=new Intent(context,CommiteeActivity.class);
                bundle=new Bundle();
                bundle.putString("Commitee",Commitee);
                intent.putExtras(bundle);//Extras not Extra
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (getLayoutPosition()==0&&use==0)
            {
                Intent intent=new Intent(context,ProfileActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("Uid","myProfile");
                intent.putExtras(bundle);//Extras not Extra
                context.startActivity(intent);
            }else if (getLayoutPosition()==1&&use==0){
                Intent intent=new Intent(context,CommiteesActivity.class);
                /*
                Bundle bundle=new Bundle();
                bundle.putString("Commitee",data.get(getLayoutPosition()).title);
                intent.putExtras(bundle);//Extras not Extra
                */
                context.startActivity(intent);
            }
            return false;
        }
    }
}
