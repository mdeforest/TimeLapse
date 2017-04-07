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
        mMediaPlayer.release();
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

    /*public int getDeviceOrientation(Context context) {

        int degrees = 0;
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        switch(rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        return degrees;
    }

    public static int getPreviewOrientation(Context context, int cameraId) {

        int temp = 0;
        int previewOrientation = 0;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int deviceOrientation = getDeviceOrientation(context);
        temp = cameraInfo.orientation - deviceOrientation + 360;
        previewOrientation = temp % 360;

        return previewOrientation;
    }*/
}
