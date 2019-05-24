package com.example.recordscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.recordscreen.R;

public class VideoPlay extends AppCompatActivity {
    private VideoView videoView;
    MediaController mediaControls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video);

        Intent intent = getIntent();
        String fileSelectPath = intent.getStringExtra("pathIntent");
        //Toast.makeText(this, fileSelectPath,Toast.LENGTH_SHORT).show();
        //Log.d("Path File Is",fileSelectPath);
        Uri fileuri = Uri.parse(fileSelectPath);

        setContentView(R.layout.play_video);
        videoView = (VideoView) findViewById(R.id.playVideo);

        if (mediaControls == null) {
            mediaControls = new MediaController(this);
            mediaControls.setAnchorView(videoView);
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        videoView.setMediaController(mediaControls);
        videoView.setVideoURI(fileuri);
        videoView.start();


    }
}