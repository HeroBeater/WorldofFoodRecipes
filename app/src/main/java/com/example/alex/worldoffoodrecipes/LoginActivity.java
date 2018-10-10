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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail, mLoginPassword;
    private Button mLoginButton, mNewAccount;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button) findViewById(R.id.login_btn);
        mNewAccount = (Button) findViewById(R.id.b_newAccount);

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerAct   = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerAct);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please wait");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mProgressDialog.show();
                    loginUser(email, password);
                }else {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                }
            }

            private void loginUser(String email, String password) {
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mProgressDialog.hide();
                            Intent mainAct = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(mainAct);
                            finish();
                        }else {
                            mProgressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error logging in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}