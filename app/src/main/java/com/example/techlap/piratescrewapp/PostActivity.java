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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton ImgButton;
    private EditText mPostTitle,mPostDesc;
    private Button subButton;
    private Uri imgUri=null;
    final int GALLERY_REQUEST=1;
    private StorageReference fireStorage;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;
    private Toolbar toolbar;
    private String name,email,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        name=getIntent().getExtras().getString("name");
        email=getIntent().getExtras().getString("email");
        image=getIntent().getExtras().getString("image");

        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Add Post");
        mPostDesc=(EditText)findViewById(R.id.descField);
        mPostTitle=(EditText)findViewById(R.id.titleField);
        subButton=(Button)findViewById(R.id.subbut);

        fireStorage=FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Posts");

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

                mprogress.setMessage("Posting..");
                if (checkInternetConenction(PostActivity.this)){
                    startPosting();
                }else{
                    Toast.makeText(PostActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
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


    private void startPosting() {
        final String tittle=mPostTitle.getText().toString().trim();
        final String desc=mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(tittle)&& !TextUtils.isEmpty(desc)&&imgUri!=null){

            mprogress.show();
            //we can use random genrated Strings
            StorageReference ImgRef=fireStorage.child("PostsImages").child(imgUri.getLastPathSegment());
            ImgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost=mdatabase.push();
                    newPost.child("title").setValue(tittle);
                    newPost.child("desc").setValue(desc);
                    newPost.child("image").setValue(downloadUrl.toString());
                    //timestamp
                    newPost.child("time").setValue(-1*System.currentTimeMillis());

                    Toast.makeText(PostActivity.this,"Posted",Toast.LENGTH_SHORT).show();
                    mprogress.dismiss();
                    Intent intent = new Intent(PostActivity.this, Main2Activity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("name",name);
                    bundle.putString("email",email);
                    bundle.putString("image",image);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mprogress.dismiss();
                    Toast.makeText(PostActivity.this,"Error",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostActivity.this, Main2Activity.class);
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("image",image);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            imgUri=data.getData();
            ImgButton.setImageURI(imgUri);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(PostActivity.this, Main2Activity.class);
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
}
