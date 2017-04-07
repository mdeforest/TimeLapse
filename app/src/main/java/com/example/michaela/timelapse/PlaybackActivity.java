package com.example.michaela.timelapse;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;

public class PlaybackActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
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


}
