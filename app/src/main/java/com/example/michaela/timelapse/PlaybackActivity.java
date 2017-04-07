package com.example.michaela.timelapse;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

/*public class PlaybackActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
{
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;
    private SurfaceHolder mSurfaceHolder;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("Video File");
        Log.d("Path", videoPath);

        mSurfaceView = (SurfaceView) findViewById(R.id.playback);

        //mSurfaceView.setOnClickListener(videoViewClickHandler);
        //mSurfaceView.setSurfaceTextureListener();
        //createMediaPlayer();
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(PlaybackActivity.this);

    }


    @Override
    protected void onResume(){
        super.onResume();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        *//*
         * the MediaController will hide after 3 seconds - tap the screen to
         * make it appear again
         *//*
        mMediaController.show();
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mMediaPlayer.setDataSource(videoPath);
            mMediaPlayer.setOnPreparedListener(PlaybackActivity.this);
            float speed = 0.25f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));

            //int rotation = getPreviewOrientation((Context)PlaybackActivity.this, 1);
            //mMediaPlayer.setOrientationHint(rotation);

            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        //while (mMediaPlayer.isPlaying())
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseMediaPlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //mediacontroller implemented methods

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }
}*/

public class PlaybackActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        videoView = (VideoView)findViewById(R.id.playback_video);

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

