package com.example.techlap.piratescrewapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends AppCompatActivity {

    private EditText mTaskTitle,mTaskDesc;
    private Button subButton,selectButton,selectFile;
    private DatabaseReference mdatabase,mtaskDB;
    private FirebaseAuth mAuth;
    private StorageReference fireStorage;
    private Toolbar toolbar;
    private Query commiteeMembersQuery;
    private ArrayList<String> listNames=null;
    private ArrayList<String>listIds=null;
    private ArrayList<String>listSelectedIds=null;
    private TextView confirmText;
    private String committe="none";
    private int FILE_REQUEST=1;
    private Uri fileUri =null;
    private ProgressDialog mprogress;
    private Uri downloadUrl=null;
    private int Use;
    private static final int PLACE_PICKER_REQUEST = 3;
    private Meeting meeting;
    private String name,email,image,Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Use=getIntent().getExtras().getInt("Use");
        if (Use==1)
        {
            setContentView(R.layout.activity_task);
        }else{
            setContentView(R.layout.activity_hold_meeting);
        }

        name=getIntent().getExtras().getString("name");
        email=getIntent().getExtras().getString("email");
        image=getIntent().getExtras().getString("image");

        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        fireStorage= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        //to get members Ids& names
        Uid=mAuth.getCurrentUser().getUid();

        listNames = new ArrayList<String>();
        listIds=new ArrayList<String>();
        listSelectedIds=new ArrayList<String>();
        committe=getIntent().getExtras().getString("Commitee");
        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Log.d("CHECK22","Hi User found{"+committe+"}");

        if (Use==1)
        {
            mTaskTitle=(EditText)findViewById(R.id.titleTaskField);
            mTaskDesc=(EditText)findViewById(R.id.descTaskField);
            subButton=(Button)findViewById(R.id.subbut);
            selectButton=(Button)findViewById(R.id.selectMembersButton);
            selectFile=(Button)findViewById(R.id.selectFileButton);
            confirmText=(TextView)findViewById(R.id.confirmSelection);

        }else{
            mTaskTitle=(EditText)findViewById(R.id.titleTaskField);//Date
            mTaskDesc=(EditText)findViewById(R.id.descTaskField);
            subButton=(Button)findViewById(R.id.subbut);//holdButton
            selectButton=(Button)findViewById(R.id.selectMembersButton);
            selectFile=(Button)findViewById(R.id.selectFileButton);
            confirmText=(TextView)findViewById(R.id.confirmSelection);
        }
        mprogress=new ProgressDialog(this);
        mprogress.setCancelable(false);
        mprogress.setCanceledOnTouchOutside(false);
        mprogress.setIndeterminateDrawable(getResources().getDrawable(R.drawable.animate_axis));



        if (Use==1) {
            setTitle("Send Task");
            mtaskDB= FirebaseDatabase.getInstance().getReference().child("Tasks");
        }else{
            setTitle("Hold Meeting");
            mtaskDB= FirebaseDatabase.getInstance().getReference().child("Meeting");
        }

        getMembers();

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMembersDialoge();
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CHECK22",""+listNames.isEmpty());
                if(Use==1)
                {
                    if (checkInternetConenction(TaskActivity.this)){
                        sendFile();
                    }else{
                        Toast.makeText(TaskActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    holdMeeting();
                }
            }
        });
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Use==1)
                {
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent,"Select Pdf"),FILE_REQUEST);

                }else{
                    locationPlacesIntent();
                }

            }
        });
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
    protected void onStart() {
        super.onStart();

    }

    private void showMembersDialoge() {
        // Intialize  readable sequence of char values
        //list of what would be shown in the dialogue
        final CharSequence[] dialogList=  listNames.toArray(new CharSequence[listNames.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(TaskActivity.this);
        builderDialog.setTitle("Submit to");

        int count = dialogList.length;
        // to know if certain member choosen or not
        boolean[] is_checked = new boolean[count];

        // Creating multiple selection by using setMutliChoiceItem method

        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {

                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            // to know the selected members
                            boolean checked = list.isItemChecked(i);
                            if (checked) {
                                listSelectedIds.add(listIds.get(i));
                                if (stringBuilder.length() > 0) stringBuilder.append(",");
                                stringBuilder.append(list.getItemAtPosition(i));
                            }
                        }

                        Log.d("CHECK22","list selected = "+listSelectedIds.get(0).toString()+"listIds = "+listIds.get(0).toString());
                        confirmText.setText("You Selected :"+stringBuilder);
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

    private void getMembers() {

        Log.d("CHECK22","If Test ="+!committe.equals("none")+"{"+committe+"}");
        if(!committe.equals("none")){
            commiteeMembersQuery = mdatabase.orderByChild("commitee").equalTo(committe);//finally done hohoooo
            commiteeMembersQuery.addValueEventListener(new ValueEventListener() {
                //get the names & ids of commitee members
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        String id=child.getKey();
                        String name=child.child("name").getValue(String.class);
                        if(!id.equals(Uid)){
                            listIds.add(id);
                            listNames.add(name);
                        }
                        Log.d("CHECK22",id+name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("CHECK22",""+"Error Data");
                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==FILE_REQUEST&&resultCode==RESULT_OK){
            fileUri=data.getData();
        }
        if(requestCode==PLACE_PICKER_REQUEST&&resultCode==RESULT_OK){
            Place place = PlacePicker.getPlace(this, data);
            if (place!=null){
                LatLng latLng = place.getLatLng();
                meeting=new Meeting("","",latLng.latitude,latLng.longitude);
            }else{
                //PLACE IS NULL
            }
        }

    }
    private void sendFile(){
        if(fileUri!=null){
            mprogress.show();
            //we can use random genrated Strings
            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);
            StorageReference ImgRef=fireStorage.child("BoardFiles").child(mAuth.getCurrentUser().getUid()+seconds);
            ImgRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl=taskSnapshot.getDownloadUrl();
                    sendTask();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error22",e.getMessage());
                    mprogress.dismiss();

                    Toast.makeText(TaskActivity.this,"Error",Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(TaskActivity.this,"Select your file ..",Toast.LENGTH_SHORT).show();
        }
    }
    private void sendTask(){
        String title=mTaskTitle.getText().toString();
        String desc =mTaskDesc.getText().toString();
        Date d = new Date();
        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
        if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(desc)&&fileUri!=null){
            //DatabaseReference newTask=mtaskDB.push();
            Task taskInfo=new Task(title,desc,mAuth.getCurrentUser().getUid(),downloadUrl.toString(),-1*System.currentTimeMillis(),false);
            for (String id:listSelectedIds){
                mtaskDB.child(id).push().setValue(taskInfo);
            }
            /*
            newTask.child("from").setValue(mAuth.getCurrentUser().getUid());
            newTask.child("file").setValue(downloadUrl.toString());
            newTask.child("title").setValue(title);
            newTask.child("desc").setValue(desc);
            for (String id:listSelectedIds){
                newTask.child("to").push().setValue(id);
            }*/
            mprogress.dismiss();
            Toast.makeText(TaskActivity.this,"Done ..",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TaskActivity.this, Main2Activity.class);
            Bundle bundle=new Bundle();
            bundle.putString("name",name);
            bundle.putString("email",email);
            bundle.putString("image",image);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else{
            mprogress.dismiss();
            Toast.makeText(TaskActivity.this,"Not Valid Task..",Toast.LENGTH_SHORT).show();
        }
    }
    private void holdMeeting() {
        String date=mTaskTitle.getText().toString();
        String desc=mTaskDesc.getText().toString();
        if(!TextUtils.isEmpty(date))
        {
            mprogress.show();
            meeting.setDate(date);
            meeting.setDsecPlace(desc);
            for (String id:listSelectedIds){
                mtaskDB.child(id).push().setValue(meeting);
            }
            mprogress.dismiss();
            Toast.makeText(TaskActivity.this,"Done ..",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TaskActivity.this,Main2Activity.class));
        }else {
            Toast.makeText(TaskActivity.this,"Write The Date..",Toast.LENGTH_SHORT).show();
        }


    }
    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(TaskActivity.this, Main2Activity.class);
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskActivity.this, Main2Activity.class);
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("image",image);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
