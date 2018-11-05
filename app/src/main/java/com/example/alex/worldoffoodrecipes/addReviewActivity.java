package com.example.alex.worldoffoodrecipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addReviewActivity extends AppCompatActivity {

    private EditText titleReview, descriptionReview;
    private TextView userField;
    private RatingBar ratingBar;
    private Float rating;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        titleReview = findViewById(R.id.editTextOfTitleOfReview);
        descriptionReview = findViewById(R.id.editTextOfDescOfReview);
        ratingBar = findViewById(R.id.ratingBar);
        userField = findViewById(R.id.textViewUser);
        Button addButton = findViewById(R.id.buttonAddReview);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            userField.setText(doc.get("Name").toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {}
                });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingBar.getRating();
                Map<String, Object> map = new HashMap<>();
                map.put("Title", titleReview.getText().toString());
                map.put("Description", descriptionReview.getText().toString());
                map.put("Author_of_review",userField.getText().toString());
                map.put("Rating",rating);

                final DocumentReference dbRef = db.collection("All Recipes").document(getIntent().getStringExtra("ID"));

                dbRef.collection("Reviews").document().set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Review saved", Toast.LENGTH_LONG).show();
                                db.collection("All Recipes").whereEqualTo("Recipe_ID", getIntent().getStringExtra("ID")).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        dbRef.update("Total_ratings",document.getDouble("Total_ratings")+rating);
                                                        dbRef.update("Number_of_reviews",document.getDouble("Number_of_reviews")+1);
                                                        double rate = (document.getDouble("Total_ratings")+rating)/(document.getDouble("Number_of_reviews")+1);
                                                        if((rate%10)<5){
                                                            dbRef.update("Average_rating", (int)(rate));
                                                        }else{
                                                            dbRef.update("Average_rating", (int)(rate+1));
                                                        }
                                                    }
                                                } else {
                                                    Log.d("addReviewActivity", "Error", task.getException());
                                                }
                                            }
                                        });
                                Intent intent = new Intent(addReviewActivity.this, RecipeShowActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("recipe_ID", getIntent().getStringExtra("ID"));
                                intent.putExtra("recipe_user", getIntent().getStringExtra("recipe_user"));
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Review NOT saved", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(addReviewActivity.this, RecipeShowActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("recipe_ID", getIntent().getStringExtra("ID"));
        intent.putExtra("recipe_user", getIntent().getStringExtra("recipe_user"));
        startActivity(intent);
        finish();
    }
}
