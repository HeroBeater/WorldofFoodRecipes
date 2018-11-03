package com.example.alex.worldoffoodrecipes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import java.util.ArrayList;

public class FullImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        ImageView imageView = findViewById(R.id.full_image_view);
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);

        imageView.setImageBitmap(bitmap);
    }

}