package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecipeShowActivity extends AppCompatActivity {

    private TextView textTitle, textSum, textDesc;
    private ImageView imageRecipe;
    private RatingBar ratingBar;
    private Button addReview;

    private RecyclerView recyclerViewReview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_show);

        recyclerViewReview = findViewById(R.id.recyclerViewReview);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(mLayoutManager);

        textTitle = findViewById(R.id.titleOfRecipe);
        textSum = findViewById(R.id.summaryOfRecipe);
        textDesc = findViewById(R.id.descriptionOfRecipe);
        imageRecipe = findViewById(R.id.mainImageRecipe);
        imageRecipe.setImageResource(R.drawable.ic_launcher_foreground);
        ratingBar = findViewById(R.id.ratingBar);
        addReview = findViewById(R.id.addReview);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final String recipe_from_intent;

        if(getIntent().hasExtra("recipe_name")){
            recipe_from_intent = getIntent().getStringExtra("recipe_name");
        }else{
            recipe_from_intent = "error recipe";
        }

        db.collection("All Recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (snapshot.getString("Title").equals(recipe_from_intent)){
                        textTitle.setText(snapshot.getString("Title"));
                        textSum.setText(snapshot.getString("Summary"));
                        textDesc.setText(snapshot.getString("Description"));
                        ratingBar.setRating(snapshot.getLong("Average_rating").intValue());
                    }
                }
            }
        });

        db.collection("All Reviews").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Review> review_list = new ArrayList<>();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    review_list.add(new Review(snapshot.getString("Author"),snapshot.getString("Title"),
                            snapshot.getString("Description"),snapshot.getLong("Rating").intValue()));
                }
                mAdapter = new ReviewAdapter(review_list,RecipeShowActivity.this);
                recyclerViewReview.setAdapter(mAdapter);
            }
        });

        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeShowActivity.this, addReviewActivity.class);
                startActivity(intent);
            }
        });

    }

}
