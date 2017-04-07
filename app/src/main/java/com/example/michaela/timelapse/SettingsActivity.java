package com.example.michaela.timelapse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    Integer frameInt;
    String qualityChoice;
    String modeChoice;
    String unitChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Sets frameInterval layout visibility based on ModeGroup choice
        RadioGroup modeGroup = (RadioGroup) findViewById(R.id.mode_choice);
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                Log.d(TAG, "Mode clicked");
                RadioGroup modeGroup = (RadioGroup) findViewById(R.id.mode_choice);
                int modeChoiceInt = modeGroup.getCheckedRadioButtonId();

                if (modeChoiceInt == R.id.mode_choice_auto) {
                    LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
                    frameIntLayout.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
                    frameIntLayout.setVisibility(View.GONE);
                }
            }
        });

        //Gather shared Preferences and prepopulate choices
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        modeChoice = sharedPref.getString("Mode", "auto");
        frameInt = sharedPref.getInt("Frame Interval", 2);
        qualityChoice = sharedPref.getString("Quality", "high");
        unitChoice = sharedPref.getString("Unit", "Milliseconds");

        //Mode
        if (modeChoice.equals("manual")) {
            modeGroup.check(R.id.mode_choice_manual);
        } else {
            modeGroup.check(R.id.mode_choice_auto);
        }

        //Frame Interval
        if (modeChoice.equals("auto")) {
            LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
            frameIntLayout.setVisibility(View.VISIBLE);
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

        } else {
            LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
            frameIntLayout.setVisibility(View.GONE);
        }

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

        boolean hasNotChanged = checkIfSame(frameInt, qualityChoice, modeChoice, unitChoice);

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
    public boolean checkIfSame(Integer frameInt, String qualityChoice, String modeChoice, String unitChoice) {
        //Mode
        RadioGroup modeGroup = (RadioGroup) findViewById(R.id.mode_choice);
        int modeChoiceInt = modeGroup.getCheckedRadioButtonId();

        String modeChoiceNew = "auto";

        if (modeChoiceInt == R.id.mode_choice_manual) {
            modeChoiceNew = "manual";
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

        return frameInt == frameIntNew && qualityChoice.equals(qualityChoiceNew) && modeChoice.equals(modeChoiceNew)
                && unitChoice.equals(unitChoiceNew);

    }


    //save Current Settings
    public void saveSettings(View v) {
        //Save in shared Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;
        SharedPreferences.Editor editor = sharedPref.edit();

        //Mode
        RadioGroup modeGroup = (RadioGroup) findViewById(R.id.mode_choice);
        int modeChoiceInt = modeGroup.getCheckedRadioButtonId();

        modeChoice = "auto";

        if (modeChoiceInt == R.id.mode_choice_manual) {
            modeChoice = "manual";
        }

        //Frame Interval
        LinearLayout frameIntLayout = (LinearLayout) findViewById(R.id.frame_interval);
        EditText frameIntText = (EditText) findViewById(R.id.frame_interval_choice);

        Spinner spinner = (Spinner) findViewById(R.id.frame_interval_type_spinner);

        //Setting visibility
        if(frameIntLayout.getVisibility() == View.VISIBLE) {
            frameInt = Integer.valueOf(frameIntText.getText().toString());
            unitChoice = spinner.getSelectedItem().toString();
            Log.d(TAG, String.valueOf(frameInt));
        }

        //Quality
        RadioGroup qualityGroup = (RadioGroup) findViewById(R.id.quality_choice);
        int qualityChoiceInt = qualityGroup.getCheckedRadioButtonId();

        qualityChoice = "high";

        if (qualityChoiceInt == R.id.quality_choice_low) {
            qualityChoice = "low";
        }

        editor.putString("Mode", modeChoice);
        editor.putInt("Frame Interval", frameInt);
        editor.putString("Unit", unitChoice);
        editor.putString("Quality", qualityChoice);
        editor.apply();

        Log.d(TAG, String.valueOf(sharedPref.getInt("Frame Interval", 2)));


        //return to Main Activity
        finish();
    }

}
