package com.example.alex.worldoffoodrecipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView myRec = findViewById(R.id.myRecipes);
        ImageView allRec = findViewById(R.id.allRecipes);
        ImageView favRec = findViewById(R.id.favRecipes);
        FloatingActionButton chatButton = findViewById(R.id.chatButton);

        myRec.setImageResource(R.mipmap.my_recipes);
        allRec.setImageResource(R.mipmap.allrecipes);
        favRec.setImageResource(R.mipmap.favorite_recipes);

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

        favRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMyRecipes   = new Intent(MainActivity.this, FavoriteRecipesActivity.class);
                startActivity(goToMyRecipes);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Global Chat Room")
                        .setMessage("Are you sure you want to enter the Chat room?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goToMyRecipes   = new Intent(MainActivity.this, GlobalRoom.class);
                                startActivity(goToMyRecipes);
                            }
                        })
                        .setNegativeButton("Maybe later", null)
                        .show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
