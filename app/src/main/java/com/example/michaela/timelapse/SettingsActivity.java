package com.example.michaela.timelapse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    Integer frameInt;
    String qualityChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Gather shared Preferences and prepopulate choices
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        frameInt = sharedPref.getInt("Frame Interval", 2);
        qualityChoice = sharedPref.getString("Quality", "high");

        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);
        frameIntText.setText(String.valueOf(frameInt));
        frameIntText.setSelection(frameIntText.getText().length());

        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);

        if (qualityChoice.equals("low")) {
            qualityGroup.check(R.id.quality_choice_low);
        } else {
            qualityGroup.check(R.id.quality_choice_high);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed");

        boolean hasNotChanged = checkIfSame(frameInt, qualityChoice);

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

    public boolean checkIfSame(Integer frameInt, String qualityChoice) {
        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);
        int frameIntNew = Integer.valueOf(frameIntText.getText().toString());

        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);
        int qualityChoiceInt = qualityGroup.getCheckedRadioButtonId();

        String qualityChoiceNew = "high";

        if (qualityChoiceInt == R.id.quality_choice_low) {
            qualityChoiceNew = "low";
        }

        if (frameInt == frameIntNew && qualityChoice.equals(qualityChoiceNew)) {
            return true;
        }

        return false;
    }


    //save Current Settings
    public void saveSettings(View v) {
        //Save in shared Preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);
        int frameInt = Integer.valueOf(frameIntText.getText().toString());

        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);
        int qualityChoiceInt = qualityGroup.getCheckedRadioButtonId();

        String qualityChoice = "high";

        if (qualityChoiceInt == R.id.quality_choice_low) {
            qualityChoice = "low";
        }

        editor.putInt("Frame Interval", frameInt);
        editor.putString("Quality", qualityChoice);
        editor.apply();


        //return to Main Activity
        finish();
    }

}
