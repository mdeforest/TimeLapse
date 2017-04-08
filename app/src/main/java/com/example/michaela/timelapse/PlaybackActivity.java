package com.example.michaela.timelapse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

//Activity to playback a given timelapse recording
public class PlaybackActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        videoView = (VideoView)findViewById(R.id.playback_video);

        //get the video filename that was selected
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("Video File");
        Log.d("Path", videoPath);
        videoView.setVideoPath(videoPath);

        if (mediaController == null) {
            // create an object of media controller
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
        }
        // set media controller object for a video view
        videoView.setMediaController(mediaController);
        videoView.start();

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });


    }
}

