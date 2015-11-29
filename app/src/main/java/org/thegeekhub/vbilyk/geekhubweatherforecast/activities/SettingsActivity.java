package org.thegeekhub.vbilyk.geekhubweatherforecast.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.frame_layout_settings, new SettingsFragment()).commit();
    }
}
