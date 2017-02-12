package com.example.techlap.piratescrewapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailF,nameF,passwordF;
    private Button regbtn;
    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mprogress=new ProgressDialog(this);

        emailF=(EditText)findViewById(R.id.emailField);
        nameF=(EditText)findViewById(R.id.nameField);
        passwordF=(EditText)findViewById(R.id.passwordField);
        regbtn=(Button)findViewById(R.id.regBtn);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        final String name=nameF.getText().toString().trim();
        final String email=emailF.getText().toString().trim();
        String password=passwordF.getText().toString().trim();

        //TODO: add more conditions here
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){

            mprogress.setMessage("Singing Up..");
            mprogress.show();
            Intent intent =new Intent(RegisterActivity.this,SetUpActivity.class);
            //enable back
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle= new Bundle();

            bundle.putString("email",email);
            bundle.putString("password",password);
            bundle.putString("name",name);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }
}
