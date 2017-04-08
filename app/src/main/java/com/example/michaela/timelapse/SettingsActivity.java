package com.example.michaela.timelapse;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

//activity for user to select settings for the timelapse camera and save them
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    Integer frameInt;
    String qualityChoice;
    String cameraChoice;
    String unitChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup cameraGroup = (RadioGroup) findViewById(R.id.camera_choice);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        cameraChoice = sharedPref.getString("Camera", "rear");
        frameInt = sharedPref.getInt("Frame Interval", 2);
        qualityChoice = sharedPref.getString("Quality", "high");
        unitChoice = sharedPref.getString("Unit", "Milliseconds");

        //Mode
        if (cameraChoice.equals("front")) {
            cameraGroup.check(R.id.camera_choice_front);
        } else {
            cameraGroup.check(R.id.camera_choice_rear);
        }

        //Frame Interval
        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);
        frameIntText.setText(String.valueOf(frameInt));
        frameIntText.setSelection(frameIntText.getText().length());

        Spinner spinner = (Spinner) findViewById(R.id.frame_interval_type_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frame_interval_type_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        int position = adapter.getPosition(unitChoice);
        spinner.setSelection(position);

        //Quality
        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);

        if (qualityChoice.equals("low")) {
            qualityGroup.check(R.id.quality_choice_low);
        } else {
            qualityGroup.check(R.id.quality_choice_high);
        }

    }

    //Create alert dialog if back button is pressed and settings have changed
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed");

        boolean hasNotChanged = checkIfSame(frameInt, qualityChoice, cameraChoice, unitChoice);

        if (!hasNotChanged) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked Yes button

                    Button settingsButton = (Button) findViewById(R.id.settings_button);
                    saveSettings(settingsButton);

                    SettingsActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked No button

                    SettingsActivity.super.onBackPressed();
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            super.onBackPressed();
        }

    }

    //Checks if settings have not changed from last save
    public boolean checkIfSame(Integer frameInt, String qualityChoice, String cameraChoice, String unitChoice) {
        //Mode
        RadioGroup cameraGroup = (RadioGroup) findViewById(R.id.camera_choice);
        int cameraChoiceInt = cameraGroup.getCheckedRadioButtonId();

        String cameraChoiceNew = "rear";

        if (cameraChoiceInt == R.id.camera_choice_front) {
            cameraChoiceNew = "front";
        }

        //Frame Interval
        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);
        int frameIntNew = Integer.valueOf(frameIntText.getText().toString());

        Spinner spinner = (Spinner) findViewById(R.id.frame_interval_type_spinner);
        String unitChoiceNew = spinner.getSelectedItem().toString();

        //Quality
        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);
        int qualityChoiceInt = qualityGroup.getCheckedRadioButtonId();

        String qualityChoiceNew = "high";

        if (qualityChoiceInt == R.id.quality_choice_low) {
            qualityChoiceNew = "low";
        }

        return frameInt == frameIntNew && qualityChoice.equals(qualityChoiceNew) && cameraChoice.equals(cameraChoiceNew)
                && unitChoice.equals(unitChoiceNew);

    }


    //save Current Settings
    public void saveSettings(View v) {
        //Save in shared Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;
        SharedPreferences.Editor editor = sharedPref.edit();

        //Mode
        RadioGroup cameraGroup = (RadioGroup) findViewById(R.id.camera_choice);
        int cameraChoiceInt = cameraGroup.getCheckedRadioButtonId();

        cameraChoice = "rear";

        if (cameraChoiceInt == R.id.camera_choice_front) {
            cameraChoice = "front";
        }

        //Frame Interval
        LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);

        Spinner spinner = (Spinner) findViewById(R.id.frame_interval_type_spinner);

        frameInt = Integer.valueOf(frameIntText.getText().toString());
        unitChoice = spinner.getSelectedItem().toString();
        Log.d(TAG, String.valueOf(frameInt));

        //Quality
        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);
        int qualityChoiceInt = qualityGroup.getCheckedRadioButtonId();

        qualityChoice = "high";

        if (qualityChoiceInt == R.id.quality_choice_low) {
            qualityChoice = "low";
        }

        editor.putString("Camera", cameraChoice);
        editor.putInt("Frame Interval", frameInt);
        editor.putString("Unit", unitChoice);
        editor.putString("Quality", qualityChoice);
        editor.apply();

        Log.d(TAG, String.valueOf(sharedPref.getInt("Frame Interval", 2)));


        //return to Main Activity
        finish();
    }

}
