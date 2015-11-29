package org.thegeekhub.vbilyk.geekhubweatherforecast.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.DetailsFragment;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int forecastId = getIntent().getIntExtra("forecastId", 0);

        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("forecastId", forecastId);
        detailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_weather_details, detailsFragment).commit();
    }
}