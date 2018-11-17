package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.Objects;

public class FullscreenVideoActivity extends AppCompatActivity {

    private VideoView videoRecipe;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_video);

        videoRecipe = findViewById(R.id.videoView);

        if (Objects.requireNonNull(getIntent().getExtras().getString("what")).equals("no")) {
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
        }else{
            String PathVideo = getIntent().getExtras().getString("pathURL");
            progDailog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ... (can take some time)", false);
            videoRecipe.setVideoURI(Uri.parse(PathVideo));
            MediaController mediacontroller = new MediaController(FullscreenVideoActivity.this);
            mediacontroller.setAnchorView(videoRecipe);
            videoRecipe.setMediaController(mediacontroller);
            videoRecipe.requestFocus();
            videoRecipe.setZOrderOnTop(true);
            videoRecipe.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("ClickableViewAccessibility")
                public void onPrepared(MediaPlayer mp) {
                    progDailog.dismiss();
                    videoRecipe.setBackgroundColor(Color.TRANSPARENT);
                    videoRecipe.start();
                }
            });
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    finish();
                }
            });
            progDailog.setCancelable(true);
        }
    }

    public void onBackPressed() {

    }
}
