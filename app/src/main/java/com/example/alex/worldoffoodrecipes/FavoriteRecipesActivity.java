package com.example.alex.worldoffoodrecipes;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FavoriteRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        updateView();
    }

    public void updateView(){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Fav Recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                ArrayList<Recipe> recipes_list = new ArrayList<>();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    if (Objects.equals(snapshot.getString("Public"), "yes")){
                        recipes_list.add(new Recipe(snapshot.getString("Author_of_recipe"), snapshot.getString("Recipe_ID"),snapshot.getString("Title"),
                                snapshot.getString("Key_words"),snapshot.getString("Description"),snapshot.getDouble("Average_rating")));
                    }
                }
                mAdapter = new RecipeAdapter(recipes_list,FavoriteRecipesActivity.this);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) FavoriteRecipesActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            final SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(FavoriteRecipesActivity.this.getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(!newText.equals("")){
                        getSearch(newText);
                    }else{
                        updateView();
                    }
                    return true;
                }
            });

            ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EditText et = findViewById(R.id.search_src_text);
                    et.setText("");
                    searchView.setQuery("", false);
                    searchView.onActionViewCollapsed();
                    menu.findItem(R.id.search).collapseActionView();
                    updateView();
                }
            });
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
                Toast.makeText(getApplicationContext(),"Add Review",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete_recipe:
                Toast.makeText(getApplicationContext(),"Delete Recipe",Toast.LENGTH_SHORT).show();
                break;
            default:
                //unknown error
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSearch(final String s){

        mAdapter = new RecipeAdapter(new ArrayList<Recipe>(),FavoriteRecipesActivity.this);

        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Fav Recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipes_list = new ArrayList<>();
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if(document.getString("Title").toLowerCase().contains(s.toLowerCase())||
                                        document.getString("Key_words").toLowerCase().contains(s.toLowerCase())){
                                    recipes_list.add(new Recipe(document.getString("Title"),
                                            document.getString("Key_words"),document.getString("Description"),
                                            document.getDouble("Average_rating")));
                                }
                            }
                            mAdapter = new RecipeAdapter(recipes_list,FavoriteRecipesActivity.this);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("addReviewActivity", "Error", task.getException());
                        }
                    }
                });
    }
}