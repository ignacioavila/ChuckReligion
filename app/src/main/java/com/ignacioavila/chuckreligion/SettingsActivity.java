package com.ignacioavila.chuckreligion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    EditText editLastName,editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView jokesLoadedfinalvalueText = (TextView) findViewById(R.id.jokesLoadedfinalvalueText);
        editName = (EditText)findViewById(R.id.editName);
        editLastName = (EditText)findViewById(R.id.editLastName);

        SeekBar volumeControl = (SeekBar) findViewById(R.id.jokesLoadedProgress);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress < MainActivity.JOKES_LOADED_MIN ? MainActivity.JOKES_LOADED_MIN : progress;
                String progressString = String.valueOf(progress);
                jokesLoadedfinalvalueText.setText(progressString);
                insertJokesLoadedPreferences(progressString);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        int jokesLoaded = "".equals(Util.getPreference(this, Util.JOKES_LOADED)) ? 0 : Integer.valueOf(Util.getPreference(this, Util.JOKES_LOADED));
        volumeControl.setProgress(jokesLoaded);

        CheckBox checkName = (CheckBox)findViewById(R.id.checkName);
        checkName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                insertCheckNamePreferences(isChecked);
                habilitarCustomNames(isChecked);
            }
        });
        checkName.setChecked(Util.getPreferenceBoolean(this, Util.CHECK_NAME_CUSTOM));

        TextWatcher watch = new TextWatcher(){

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                insertCustomName(s.toString());
            }
        };
        editName.addTextChangedListener(watch);
        editName.setText(Util.getPreference(this, Util.NAME_CUSTOM));

        TextWatcher watchLastName = new TextWatcher(){

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                insertCustomLastName(s.toString());

            }
        };
        editLastName.addTextChangedListener(watchLastName);
        editLastName.setText(Util.getPreference(this, Util.LASTNAME_CUSTOM));

        habilitarCustomNames(Util.getPreferenceBoolean(this, Util.CHECK_NAME_CUSTOM));
    }

    final private void habilitarCustomNames(boolean isChecked) {
        editName.setEnabled(isChecked);
        editLastName.setEnabled(isChecked);
    }

    final void insertJokesLoadedPreferences(String value){
        Util.insertPreference(this, Util.JOKES_LOADED, value);
    }

    final String getJokesLoadedPreferences(){
        return Util.getPreference(this,Util.JOKES_LOADED);
    }

    final void insertCheckNamePreferences(boolean value){
        Util.insertPreferenceBoolean(this, Util.CHECK_NAME_CUSTOM, value);
    }

    final void insertCustomName(String value){
        Util.insertPreference(this, Util.NAME_CUSTOM, value);
    }

    final void insertCustomLastName(String value){
        Util.insertPreference(this, Util.LASTNAME_CUSTOM, value);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
