package com.example.alex.worldoffoodrecipes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName, mPassword, mPasswordRe, mEmail, mEmailRe;
    private Button mBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mEmailRe = findViewById(R.id.reg_email_re);
        mPassword = findViewById(R.id.reg_password);
        mPasswordRe = findViewById(R.id.reg_password_re);
        mBtn = findViewById(R.id.reg_btn);

        db = FirebaseFirestore.getInstance();

        mProgressDialog = new ProgressDialog(this);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String emailRe = mEmailRe.getText().toString();
                String password = mPassword.getText().toString();
                String passwordRe = mPasswordRe.getText().toString();

                if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    if(email.equals(emailRe)){
                        if(password.equals(passwordRe)){
                            if(password.length()>5){
                                mProgressDialog.setTitle("Loading...");
                                mProgressDialog.setMessage("Please wait");
                                mProgressDialog.setCanceledOnTouchOutside(false);
                                mProgressDialog.show();
                                register(username, email, md5(password));
                            }else{
                                Toast.makeText(RegisterActivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "Emails do not match!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                }
            }

            private void register(final String name, final String email, final String password) {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("Name",name);
                            userMap.put("Email",email);
                            userMap.put("Password",password);

                            db.collection("Users").document(uid).set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
