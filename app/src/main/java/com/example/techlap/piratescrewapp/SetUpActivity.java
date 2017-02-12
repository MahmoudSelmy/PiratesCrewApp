package com.example.techlap.piratescrewapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;

    private ImageButton ImgButton;
    final int GALLERY_REQUEST=1;
    private Uri imgUri=null;
    private StorageReference fireStorage;
    private ProgressDialog mprogress;
    private Button subButton;
    private EditText perInfoField,phoneField;
    private String name, email,password, commitee,position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        setTitle("Settings");

        commitee ="";
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        fireStorage= FirebaseStorage.getInstance().getReference();

        subButton=(Button)findViewById(R.id.subbut);
        perInfoField=(EditText)findViewById(R.id.personalInfo);
        phoneField=(EditText)findViewById(R.id.phone);

        name=getIntent().getExtras().getString("name");
        email=getIntent().getExtras().getString("email");
        password=getIntent().getExtras().getString("password");

        spinner=(Spinner)findViewById(R.id.postionsSpinner);

        String [] postions={"President","OM Director", "OC Director", "Marketing Director", "AC Director","Project Manager","Project Manager Assistant",
                "QM Head","QM Vice head","QM Member",
                "HR Head","HR Vice Head","HR Member",
                "IT Head","IT Member",
                "Logistics Head","Logistics Member",
                "Advertising Head","Advertising Vice Head","Advertising Member"
                ,"Social Media Head","Social Media Member"
                ,"FR Head","FR Member"
                ,"PR Head","PR Member"
                ,"Technical Track Head","Technical Track Moderator","Technical Track Vice Head","Non-Technical track head","Non-Technical Track Moderator"
                , "Online Academy Head","Online Academy Moderator"
        };

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,postions);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        ImgButton=(ImageButton)findViewById(R.id.imgSelector);
        ImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });

        mprogress=new ProgressDialog(this);
        mprogress.setCancelable(false);
        mprogress.setCanceledOnTouchOutside(false);
        mprogress.setIndeterminateDrawable(getResources().getDrawable(R.drawable.animate_axis));

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConenction(SetUpActivity.this)){
                    mprogress.setMessage("Setting Up..");
                    createAccount(name,email,password);
                }else{
                    Toast.makeText(SetUpActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void createAccount(final String name, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //we will name the User by its Id
                    String userId=mAuth.getCurrentUser().getUid();
                    DatabaseReference newUser=mdatabase.child(userId);
                    //public UserModel(String profileimage, String name, String email, String commitee, String personalInfo, String position, String phone) {
                    UserModel user=new UserModel("",name,email,commitee,"", position,"");//the last personal info
                    newUser.setValue(user);
                    setUp();
                }

            }
        });
    }
    private void setUp() {
        final String personalInfo=perInfoField.getText().toString();
        final String phone=phoneField.getText().toString();

        if(imgUri!=null&&!personalInfo.isEmpty()&&!phone.isEmpty()){

            mprogress.show();
            //we can use random genrated Strings
            StorageReference ImgRef=fireStorage.child("UsersImages").child(mAuth.getCurrentUser().getUid());
            ImgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newUser=mdatabase.child(mAuth.getCurrentUser().getUid());
                    newUser.child("profileimage").setValue(downloadUrl.toString());
                    newUser.child("personalInfo").setValue(personalInfo);
                    newUser.child("phone").setValue(phone);
                    Toast.makeText(SetUpActivity.this,"Have fun..",Toast.LENGTH_SHORT).show();
                    mprogress.dismiss();
                    startActivity(new Intent(SetUpActivity.this,SplashActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mprogress.dismiss();
                    Toast.makeText(SetUpActivity.this,"Error",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView=(TextView)view;
        position=textView.getText().toString();


        switch (position){
            case "President":
            case "OM Director":
            case "OC Director":
            case "Marketing Director":
            case "AC Director":
            case "Project Manager":
                commitee ="Board Of Directors";
                break;
            case "QM Head":
            case "QM Vice Head":
            case "QM Member":
                commitee ="QM";
                break;
            case "HR Head":
            case "HR Vice Head":
            case "HR Member":
                commitee ="HR";
                break;
            case "IT Head":
            case "IT Member":
                commitee ="IT";
                break;
            case "Logistics Head":
            case "Logistics Member":
                commitee ="Logistics";
                break;
            case "Advertising Head":
            case "Advertising Vice Head":
            case "Advertising Member":
                commitee ="Advertising";
                break;
            case "Social Media Head":
            case "Social Media Member":
                commitee ="Social Media";
                break;
            case "FR Head":
            case "FR Member":
                commitee ="FR";
                break;
            case "PR Head":
            case "PR Member":
                commitee ="PR";
                break;
            case "Technical Track Head":
            case "Technical Track Moderator":
            case "Technical Track Vice Head":
                commitee ="Technical Track";
                break;
            case "Non-Technical Track Head":
            case "Non-Technical Track Moderator":
                commitee ="Non-Technical Track";
                break;
            case "Online Academy Head":
            case "Online Academy Moderator":
                commitee ="Online Academy";
                break;
            default:
                commitee ="Project Management";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            imgUri=data.getData();
            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                ImgButton.setImageURI(imgUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

}
