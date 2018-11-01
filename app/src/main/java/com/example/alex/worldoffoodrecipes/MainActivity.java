package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myRec = findViewById(R.id.myRecipes);
        Button allRec = findViewById(R.id.allRecipes);

        myRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMyRecipes   = new Intent(MainActivity.this, myRecipesActivity.class);
                startActivity(goToMyRecipes);
            }
        });

        allRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAllRecipes = new Intent(MainActivity.this, AllRecipesActivity.class);
                startActivity(goToAllRecipes);
            }
        });

    }

}
