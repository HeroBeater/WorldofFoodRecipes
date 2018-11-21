package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Button addButton;
    private Float rating;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db;
    private Boolean update = false;
    private Float oldRating;
    private String user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        titleReview = findViewById(R.id.editTextOfTitleOfReview);
        descriptionReview = findViewById(R.id.editTextOfDescOfReview);
        ratingBar = findViewById(R.id.ratingBar);
        userField = findViewById(R.id.textViewUser);
        addButton = findViewById(R.id.buttonAddReview);
        addButton.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        invalidateOptionsMenu();

        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userField.setText("ERROR USER");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        user = task.getResult().getString("Name");
                        userField.setText(user);
                        if(getIntent().getStringExtra("new")==null){
                            if(getIntent().getStringExtra("review_u").equals(user)){
                                update = true;
                                String r_ID = getIntent().getStringExtra("ID");
                                addButton.setText("Update review");
                                db.collection("All Recipes").document(r_ID).collection("Reviews").document(mAuth.getCurrentUser().getUid())
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                titleReview.setText(task.getResult().getString("Title"));
                                                descriptionReview.setText(task.getResult().getString("Description"));
                                                userField.setText(task.getResult().getString("Author_of_review"));
                                                oldRating = (float)task.getResult().getLong("Rating");
                                                ratingBar.setRating(oldRating);
                                            }
                                        });
                            }else{
                                addButton.setVisibility(View.GONE);
                                String r_ID = getIntent().getStringExtra("ID");
                                db.collection("All Recipes").document(r_ID).collection("Reviews")
                                        .whereEqualTo("Author_of_review",getIntent().getStringExtra("review_u"))
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                        titleReview.setText(documentSnapshot.getString("Title"));
                                                        titleReview.setEnabled(false);
                                                        descriptionReview.setText(documentSnapshot.getString("Description"));
                                                        descriptionReview.setEnabled(false);
                                                        userField.setText(documentSnapshot.getString("Author_of_review"));
                                                        oldRating = (float)documentSnapshot.getLong("Rating");
                                                        ratingBar.setRating(oldRating);
                                                        ratingBar.setFocusable(false);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(update){
                    DocumentReference rev = db.collection("All Recipes").document(getIntent().getStringExtra("ID"))
                            .collection("Reviews").document(mAuth.getCurrentUser().getUid());
                    rev.update("Title", titleReview.getText().toString(),
                            "Description", descriptionReview.getText().toString(),
                            "Rating",ratingBar.getRating())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final DocumentReference dbRef = db.collection("All Recipes").document(getIntent().getStringExtra("ID"));
                                    dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    Double tt = document.getDouble("Total_ratings")-oldRating+ratingBar.getRating();
                                                    Double nn = document.getDouble("Number_of_reviews");
                                                    double rate = (tt)/(nn);
                                                    int newR;
                                                    if((rate%10)<5){
                                                        newR = (int)(rate);
                                                    }else{
                                                        newR = (int)(rate+1);
                                                    }
                                                    dbRef.update("Total_ratings",tt,
                                                            "Average_rating", newR)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(),"Review updated!",Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(addReviewActivity.this, RecipeShowActivity.class);
                                                                    intent.putExtra("recipe_ID", getIntent().getStringExtra("ID"));
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(),"ERROR update!",Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d("Recipe sea", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"ERROR update!",Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    rating = ratingBar.getRating();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Title", titleReview.getText().toString());
                    map.put("Description", descriptionReview.getText().toString());
                    map.put("Author_of_review",userField.getText().toString());
                    map.put("Rating",rating);

                    final DocumentReference dbRef = db.collection("All Recipes").document(getIntent().getStringExtra("ID"));

                    dbRef.collection("Reviews").document(mAuth.getCurrentUser().getUid()).set(map)
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
                                                            double tot = document.getDouble("Total_ratings")+rating;
                                                            double num = document.getDouble("Number_of_reviews")+1;
                                                            dbRef.update("Total_ratings", tot);
                                                            dbRef.update("Number_of_reviews", num);
                                                            double rate = (tot)/(num);
                                                            if((rate%10)<5){
                                                                dbRef.update("Average_rating", (int)(rate));
                                                            }else{
                                                                dbRef.update("Average_rating", (int)(rate+1));
                                                            }
                                                        }
                                                        Intent intent = new Intent(addReviewActivity.this, RecipeShowActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.putExtra("recipe_ID", getIntent().getStringExtra("ID"));
                                                        intent.putExtra("recipe_user", getIntent().getStringExtra("recipe_user"));
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Log.d("addReviewActivity", "Error", task.getException());
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Review NOT saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (final Menu menu) {
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("Name",getIntent().getStringExtra("review_u")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                if(mAuth.getCurrentUser().getEmail().equals(documentSnapshot.getString("Email"))){
                                    menu.getItem(4).setVisible(true);
                                }else{
                                    menu.getItem(4).setVisible(false);
                                }
                            }
                        }
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.favorite:

                break;
            case R.id.edit_recipe:

                break;
            case R.id.add_review:

                break;
            case R.id.delete_recipe:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete Review");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final DocumentReference dd = FirebaseFirestore.getInstance().collection("All Recipes")
                                .document(getIntent().getStringExtra("ID"));
                        dd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        double tot = document.getDouble("Total_ratings")-oldRating;
                                        double num = document.getDouble("Number_of_reviews")-1;
                                        double rate = (tot)/(num);
                                        int newR;
                                        if((rate%10)<5){
                                            newR = (int)(rate);
                                        }else{
                                            newR = (int)(rate+1);
                                        }
                                        dd.update("Total_ratings", tot,
                                                "Number_of_reviews",num,
                                                "Average_rating", newR)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dd.collection("Reviews").document(mAuth.getCurrentUser().getUid())
                                                                .delete()
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d("Deleting review ", "Error deleting document", e);
                                                                    }
                                                                })
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(getApplicationContext(),"Review deleted!",Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(addReviewActivity.this, RecipeShowActivity.class);
                                                                        intent.putExtra("recipe_ID",getIntent().getStringExtra("ID"));
                                                                        intent.putExtra("recipe_user",getIntent().getStringExtra("recipe_user"));
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),"ERROR update!",Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        Log.d("Recipe search", "No such document");
                                    }
                                } else {
                                    Log.d("Recipe search", "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
                break;
            default:
                //unknown error
        }

        return super.onOptionsItemSelected(item);
    }
}
