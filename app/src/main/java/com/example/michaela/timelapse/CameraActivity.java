package com.example.michaela.timelapse;
/*
 * Edited for timelapse by Michaela DeForest and Julia Ramsey
 *
 *
 * Original Code from:
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.annotation.TargetApi;
        import android.content.SharedPreferences;
        import android.hardware.Camera;
        import android.media.CamcorderProfile;
        import android.media.MediaRecorder;
        import android.media.MediaScannerConnection;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.TextureView;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.Toast;
        import java.io.File;
        import java.io.IOException;
        import java.util.List;

/**
 *  This activity uses the camera/camcorder as the A/V source for the {@link android.media.MediaRecorder} API.
 *  A {@link android.view.TextureView} is used as the camera preview which limits the code to API 14+. This
 *  can be easily replaced with a {@link android.view.SurfaceView} to run on older devices.
 */
public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;

    private boolean isRecording = false;
    private static final String TAG = "Recorder";
    private Button captureButton;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mPreview = (TextureView) findViewById(R.id.texture);
        captureButton = (Button) findViewById(R.id.button_capture);

        //holds user settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Toast.makeText(this, "Press the capture button to begin recording!", Toast.LENGTH_SHORT).show();
    }

    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link android.media.MediaRecorder} and {@link android.hardware.Camera}. When not recording,
     * it prepares the {@link android.media.MediaRecorder} and starts recording.
     *
     * @param view the view generating the event.
     */
    public void onCaptureClick(View view) {
        if (isRecording) {
            // stop recording and release camera
            try {
                //light up screen when done recording
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = 0.9f;
                getWindow().setAttributes(lp);
                mMediaRecorder.stop();
                Toast.makeText(this, "Press the capture button again to record a new time lapse!", Toast.LENGTH_SHORT).show();
                //fix bug about files not showing up in gallery after being recorded (only show up when usb is unplugged and replugged sometimes)
                MediaScannerConnection.scanFile(this, new String[]{this.mOutputFile.getAbsolutePath()}, null, null);
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                mOutputFile.delete();
            } catch (Exception e1) {
                Log.d(TAG, "outputfile is null");
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder
            isRecording = false;
            releaseCamera();

        } else {
            new MediaPrepareTask().execute(null, null, null);
            //dim screen while recording to conserve battery
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 0.005f;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder(){

        //Open rear or front camera based on user settings
        String cameraChoice = sharedPref.getString("Camera", "rear");
        if (cameraChoice.equals("front")) {
            mCamera = CameraHelper.getDefaultFrontFacingCameraInstance();
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera = CameraHelper.getDefaultBackFacingCameraInstance();
            mCamera.setDisplayOrientation(270);
        }

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());

        //change the quality of video depending on user settings
        String qualityChoice = sharedPref.getString("Quality", "high");
        CamcorderProfile profile;

        if (qualityChoice.equals("low")) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_LOW);
        } else {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH);
        }

        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);

        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        //set frame rate
        double frameRate = convertUnits();
        Log.d(TAG, "frameRate: "+frameRate);
        mMediaRecorder.setCaptureRate(frameRate);


        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                CameraActivity.this.finish();
            }

        }
    }

    //Function for onverting from different units to fps for setCaptureRate
    public double convertUnits() {
        double frameInterval = sharedPref.getInt("Frame Interval", 2);
        String unitChoice = sharedPref.getString("Unit", "Milliseconds");

        switch (unitChoice) {
            case "Milliseconds":
                return 1000/frameInterval;

            case "Seconds":
                return 1/frameInterval;

            case "Minutes":
                return 1/(60*frameInterval);

            case "Hours":
                return 1/(3600*frameInterval);

            case "Days":
                return 1/(86400*frameInterval);
            default:
                return frameInterval;
        }
    }
}