package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RecipeShowActivity extends AppCompatActivity {

    private TextView textTitle, textSum, textDesc;
    private ImageView imageRecipe;
    private RatingBar ratingBar;

    private String recipe_ID_intent;
    private String user_of_recipe;

    private RecyclerView recyclerViewReview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_show);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerViewReview = findViewById(R.id.recyclerViewReview);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(mLayoutManager);

        textTitle = findViewById(R.id.titleOfRecipe);
        textSum = findViewById(R.id.summaryOfRecipe);
        textDesc = findViewById(R.id.descriptionOfRecipe);
        imageRecipe = findViewById(R.id.mainImageRecipe);
        imageRecipe.setImageResource(R.drawable.ic_launcher_foreground);
        ratingBar = findViewById(R.id.ratingBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("All Recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (Objects.equals(snapshot.getString("Title"), getIntent().getStringExtra("recipe_name"))){
                        String recipe_ID = snapshot.getString("Recipe_ID");
                        recipe_ID_intent = recipe_ID;
                        user_of_recipe = snapshot.getString("Author_of_recipe");
                        if(snapshot.getDouble("Number_of_reviews")!=0){
                            db.collection("All Recipes").document(recipe_ID).update("Average_rating",
                                    ((Math.round(snapshot.getDouble("Total_ratings")/snapshot.getDouble("Number_of_reviews")*10.0))/10.0));
                        }
                        textTitle.setText(snapshot.getString("Title"));
                        textSum.setText(snapshot.getString("Summary"));
                        textDesc.setText(snapshot.getString("Description"));
                        ratingBar.setRating(snapshot.getLong("Average_rating"));

                        db.collection("All Recipes").document(recipe_ID).collection("Reviews").orderBy("Rating", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        ArrayList<Review> review_list = new ArrayList<>();
                                        for (DocumentSnapshot snapshot : documentSnapshots){
                                            review_list.add(new Review(snapshot.getString("Author_of_review"),snapshot.getString("Title"),
                                                    snapshot.getString("Description"), Objects.requireNonNull(snapshot.getDouble("Rating")).floatValue()));
                                        }
                                        mAdapter = new ReviewAdapter(review_list,RecipeShowActivity.this);
                                        recyclerViewReview.setAdapter(mAdapter);
                                    }
                                });
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(0).setVisible(false);
        if (!mAuth.getCurrentUser().getUid().equals(user_of_recipe)){
            menu.getItem(2).setVisible(false);
            menu.getItem(4).setVisible(false);
        }else{
            menu.getItem(1).setVisible(false);
            menu.getItem(3).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.favorite:
                Toast.makeText(getApplicationContext(),"Favorite Recipe",Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_recipe:
                Toast.makeText(getApplicationContext(),"Edit Recipe",Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_review:
                Intent intent = new Intent(RecipeShowActivity.this, addReviewActivity.class);
                intent.putExtra("ID", recipe_ID_intent);
                startActivity(intent);
                break;
            case R.id.delete_recipe:
                Toast.makeText(getApplicationContext(),"Delete Recipe",Toast.LENGTH_SHORT).show();
                break;
            default:
                //unknown error
        }

        return super.onOptionsItemSelected(item);
    }

}
