package org.thegeekhub.vbilyk.geekhubweatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.thegeekhub.vbilyk.geekhubweatherforecast.activities.DetailsActivity;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.DetailsFragment;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ForecastAdapter adapter;
    private boolean landSW600dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        landSW600dp = findViewById(R.id.frame_weather_details) != null;

        findViewById(R.id.text_time_of_weather);

//        Forecast parcelableExtra = getIntent().getParcelableExtra(Forecast.class.getSimpleName());

        final ListView listView = (ListView) findViewById(R.id.listForecast);

        String url = "http://api.openweathermap.org/data/2.5/forecast?APPID=ad8683cdea16dddedadcdac0f4a20385&id=703448&units=metric";

        new AsyncHttpClient().get(url, new BaseJsonHttpResponseHandler<Response>() {

            private Gson gson;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
                Log.d("onSuccess", rawJsonResponse);
                adapter = new ForecastAdapter(MainActivity.this, response.getList());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {
                Log.d("onFailure", rawJsonData);

            }

            @Override
            protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                            .setDateFormat("2015-11-20 18:00:00")
                            .create();
                }
                return gson.fromJson(rawJsonData, Response.class);
            }
        });
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Forecast forecast = adapter.getItem(position);
        if (landSW600dp) {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("forecast", forecast);
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_weather_details, detailsFragment).commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("forecast", forecast);
            startActivity(intent);
        }
    }
}
