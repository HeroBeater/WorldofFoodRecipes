package com.example.alex.worldoffoodrecipes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AllRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("All Recipes").orderBy("Average_rating", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Recipe> recipes_list = new ArrayList<>();
                Log.d("allRecipeActivity","query launched");
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (Objects.equals(snapshot.getString("Public"), "yes")){
                        Log.d("allRecipeActivity","query not launched");
                        recipes_list.add(new Recipe(snapshot.getString("Title"),
                                snapshot.getString("Summary"),snapshot.getString("Description"),snapshot.getDouble("Average_rating")));
                    }
                }
                mAdapter = new RecipeAdapter(recipes_list,AllRecipesActivity.this);
                recyclerView.setAdapter(mAdapter);
            }
        });

    }
}
