package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class myRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        recyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("All Recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Recipe> recipes_list = new ArrayList<>();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (Objects.equals(snapshot.getString("Author_of_recipe"), Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                        recipes_list.add(new Recipe(snapshot.getString("Title"),
                                snapshot.getString("Summary"),snapshot.getString("Description")));
                    }
                }
                mAdapter = new RecipeAdapter(recipes_list,myRecipesActivity.this);
                recyclerView.setAdapter(mAdapter);
            }
        });

        FloatingActionButton FAB = findViewById(R.id.floating_action_button);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myRecipesActivity.this,addNewRecipeActivity.class);
                startActivity(intent);
            }
        });

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;

            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.edit_recipe:
                Toast.makeText(getApplicationContext(),"Edit Recipe",Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite:
                Toast.makeText(getApplicationContext(),"Favorite Recipe",Toast.LENGTH_SHORT).show();
                break;
            default:
                //unknown error
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSearch(String s){

        mAdapter = new RecipeAdapter(new ArrayList<Recipe>(),myRecipesActivity.this);

        Query query = db.collection("All Recipes");

        query.whereEqualTo("Title", s);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        ArrayList<Recipe> recipes_list = new ArrayList<>();
                        for (DocumentSnapshot snapshot : documentSnapshots){
                            recipes_list.add(new Recipe(snapshot.getString("Title"),
                                    snapshot.getString("Summary"),snapshot.getString("Description")));
                        }
                        mAdapter = new RecipeAdapter(recipes_list,myRecipesActivity.this);
                        recyclerView.setAdapter(mAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("myRecipesActivity", e.getLocalizedMessage());
                    }
                });
    }
}
