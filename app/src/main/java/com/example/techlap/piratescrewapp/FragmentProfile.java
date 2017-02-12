package com.example.techlap.piratescrewapp;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private TextView name,mail, position,personalInfo,phone,id;
    private UserModel user;
    private String Uid;
    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.activity_profile, container, false);
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        //profilePic = (CircularImageView)view.findViewById(R.id.profileImage);
        profilePic = (ImageView)view.findViewById(R.id.profileImage);

        name=(TextView)view.findViewById(R.id.profileName);
        mail=(TextView)view.findViewById(R.id.profileEmail);
        position=(TextView)view.findViewById(R.id.profilePosition);
        phone=(TextView)view.findViewById(R.id.profilePhone);
        id=(TextView)view.findViewById(R.id.profileId);
        personalInfo=(TextView)view.findViewById(R.id.profilePersonalInfo);
        Uid=mAuth.getCurrentUser().getUid();

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(id.getText());
                Toast.makeText(getActivity(), "Id Copied", Toast.LENGTH_SHORT).show();
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLangDialog("UserName");
            }
        });
        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLangDialog("Bio");
            }
        });
        return view;
    }

    private void showChangeLangDialog(final String use) {

        if(checkInternetConenction(getActivity())){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_dialog_text_change, null);
            dialogBuilder.setView(dialogView);

            final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

            dialogBuilder.setTitle("Change "+use);
            dialogBuilder.setMessage("Enter Your "+use+" below");
            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    String child;
                    if (use.equals("UserName")){
                        child="name";
                    }else{
                        child="personalInfo";
                    }
                    if(!edt.getText().toString().isEmpty()){
                        mdatabase.child(Uid).child(child).setValue(edt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                dialog.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(getActivity(),"Field is empty",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }else{
            Toast.makeText(getActivity(),"Check Internet Connection",Toast.LENGTH_SHORT).show();
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
    public void onStart() {
        super.onStart();

        passData(mAuth.getCurrentUser().getUid());
        mdatabase.keepSynced(true);

    }
    private void passData(final String Uid) {
        mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(UserModel.class);
                name.setText(user.getName());
                id.setText(Uid);
                mail.setText(user.getEmail());
                position.setText(user.getPosition());
                personalInfo.setText(user.getPersonalInfo());
                phone.setText(user.getPhone());
                //check if it available offline or not
                if(isAdded()){
                    Picasso.with(getActivity().getApplicationContext()).load(user.getProfileimage()).networkPolicy(NetworkPolicy.OFFLINE).into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            // if it not available online we need to download it
                            Picasso.with(getActivity().getApplicationContext()).load(user.getProfileimage()).into(profilePic);
                        }
                    });
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CHECK33","Picasoo Error");
            }
        });
    }


}
