package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        recyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("All Recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Recipe> recipes_list = new ArrayList<>();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (snapshot.getString("Author").equals(mAuth.getCurrentUser().getUid())){
                        recipes_list.add(new Recipe(snapshot.getString("Title"),
                                snapshot.getString("Summary"),snapshot.getString("Description")));
                    }
                }
                mAdapter = new RecipeAdapter(recipes_list);
                recyclerView.setAdapter(mAdapter);
            }
        });

    }
}
