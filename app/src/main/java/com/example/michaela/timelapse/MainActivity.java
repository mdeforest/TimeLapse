package com.example.michaela.timelapse;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;

import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Size;
import android.util.SparseIntArray;

import android.view.TextureView;

import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();

    private boolean canUseCamera;
    private boolean canSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraPermission();
        writeExternalPermission();

    }


    /*
    * CAMERA FUNCTIONS
     */
    public void clickCapture(View v){
        /*Camera camera = null;
        if (checkCameraHardware(this)){
            camera = getCameraInstance(0);
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);

            //parameters.setRotation(180);
            setCameraDisplayOrientation(this, 0, camera);
            Log.d(TAG,"got here1");

        }
        Log.d(TAG, camera.toString());*/
        Log.d(TAG, "got here");
        //CameraActivity preview = new CameraActivity(this);
        Intent i = new Intent(this, Camera_new.class);
        startActivity(i);
        //camera.release();

    }

    /*public void clickCapture(View v){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    }*/

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }





           /*// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));

        // Step 5.5: Set the video capture rate to a low number
        mMediaRecorder.setCaptureRate(captureRate); // capture a frame every 10 seconds (.1)*/


    //Check if this device has a camera
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    //get a Camera object
    public static Camera getCameraInstance(int cameraID){
        int numCameras = Camera.getNumberOfCameras();
        Camera c = null;
        if (cameraID < numCameras) {
            try {
                c = Camera.open(cameraID); // attempt to get a Camera instance
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
            }

        }
        return c; // returns null if camera is unavailable
    }



    /*
    *PERMISSIONS and STORAGE
     */

    public void cameraPermission(){
        //ask for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else{
            canUseCamera = true;
        }
    }

    //code from https://developer.android.com/training/permissions/requesting.html
    public void writeExternalPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else{
            canSave = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseCamera = true;
                } else {
                    canUseCamera = false;
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isExternalStorageWritable()) {
                        File file = getAudioStorageDir("TimeLapse");
                        canSave= true;
                        loadSavedFiles();
                    }
                } else {
                    canSave = false;
                }
            }
        }
    }

    //loads all files in the directory to filessofar (so that they will all appear in ViewActivity)
    public void loadSavedFiles(){
        File sdCardRoot = Environment.getExternalStorageDirectory();
        ArrayList filessofar = new ArrayList<>();
        File yourDir = new File(sdCardRoot, "TimeLapse");
        if (yourDir.listFiles() != null) {
            for (File f : yourDir.listFiles()) {
                if (f.isFile())
                    filessofar.add(f.getName());
            }
        }
    }

    //gets the directory to store files in
    public File getAudioStorageDir(String folderName){
        File file = new File(Environment.getExternalStorageDirectory(), folderName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    //determines if external storage is available
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    //opens Settings when Settings button is clicked
    public void clickSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    //opens Gallery when Gallery button is clicked
    public void clickGallery(View v) {
        Intent i = new Intent(this, GalleryActivity.class);
        startActivity(i);
    }

    //Converting from different units to milliseconds for frame interval
    public long convertUnits() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        int frameInterval = sharedPref.getInt("Frame Interval", 2);
        String unitChoice = sharedPref.getString("Unit", "Milliseconds");

        long convertedUnit = frameInterval;

        switch (unitChoice) {
            case "Milliseconds":
                convertedUnit = frameInterval;
                break;

            case "Seconds":
                convertedUnit = 1000*frameInterval;
                break;

            case "Minutes":
                convertedUnit = 60000*frameInterval;
                break;

            case "Hours":
                convertedUnit = 3600000*frameInterval;
                break;

            case "Days":
                convertedUnit = 86400000*frameInterval;
                break;
        }

        return convertedUnit;
    }
}
