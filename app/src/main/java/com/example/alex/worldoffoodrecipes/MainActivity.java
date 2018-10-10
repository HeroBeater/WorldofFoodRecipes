package com.example.alex.worldoffoodrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button myRec;
    private Button allRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        myRec = (Button) findViewById(R.id.myRecipes);
        allRec = (Button) findViewById(R.id.allRecipes);

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
                Intent goToAllRecipes = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(goToAllRecipes);
            }
        });

    }

}
