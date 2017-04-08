package com.example.michaela.timelapse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();


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
    public void clickCapture(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);

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


    /*
    *PERMISSIONS and STORAGE
     */

    public void cameraPermission() {
        //ask for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    //code from https://developer.android.com/training/permissions/requesting.html
    public void writeExternalPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isExternalStorageWritable()) {
                        File file = getVideoStorageDir("TimeLapse");
                        //loadSavedFiles();
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
        }
    }

    //gets the directory to store files in
    public File getVideoStorageDir(String folderName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), folderName);
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
}



