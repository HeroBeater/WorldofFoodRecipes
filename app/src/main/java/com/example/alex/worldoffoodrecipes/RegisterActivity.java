package com.example.alex.worldoffoodrecipes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName, mPassword, mEmail;
    private Button mBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mBtn = (Button) findViewById(R.id.reg_btn);

        mProgressDialog = new ProgressDialog(this);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    mProgressDialog.setTitle("Loading...");
                    mProgressDialog.setMessage("Please wait");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    register(name, email, password);
                }
            }

            private void register(final String name, final String email, final String password) {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("email",email);
                            userMap.put("password",password);

                            mDatabase.setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgressDialog.dismiss();
                                    Intent launch = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(launch);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
