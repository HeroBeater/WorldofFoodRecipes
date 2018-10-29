package com.example.alex.worldoffoodrecipes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeShowActivity extends AppCompatActivity {

    private TextView textTitle, textSum, textDesc;
    private ImageView imageRecipe;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_show);

        textTitle = findViewById(R.id.titleOfRecipe);
        textSum = findViewById(R.id.summaryOfRecipe);
        textDesc = findViewById(R.id.descriptionOfRecipe);
        imageRecipe = findViewById(R.id.mainImageRecipe);

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
                    }
                }
            }
        });
    }

}
