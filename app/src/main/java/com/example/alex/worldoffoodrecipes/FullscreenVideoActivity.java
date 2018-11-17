package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullscreenVideoActivity extends AppCompatActivity {

    private VideoView videoRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_video);

        videoRecipe = findViewById(R.id.videoView);

        String PathVideo = getIntent().getExtras().getString("path");

        videoRecipe.setVideoURI(Uri.parse(PathVideo));
        MediaController mediacontroller = new MediaController(FullscreenVideoActivity.this);
        mediacontroller.setAnchorView(videoRecipe);
        videoRecipe.setMediaController(mediacontroller);
        videoRecipe.requestFocus();
        videoRecipe.setZOrderOnTop(true);
        videoRecipe.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("ClickableViewAccessibility")
            public void onPrepared(MediaPlayer mp) {
                videoRecipe.setBackgroundColor(Color.TRANSPARENT);
                videoRecipe.start();
            }
        });
    }

}
