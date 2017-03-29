package com.example.michaela.timelapse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.util.ArrayList;

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
        Camera camera = null;
        if (checkCameraHardware(this)){
            camera = getCameraInstance(0);

        }
        Log.d(TAG, camera.toString());
        Log.d(TAG, "got here");
        //CameraActivity preview = new CameraActivity(this);
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
        camera.release();

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

}
