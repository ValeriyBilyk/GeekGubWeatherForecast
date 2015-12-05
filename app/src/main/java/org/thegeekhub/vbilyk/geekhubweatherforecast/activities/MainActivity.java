package org.thegeekhub.vbilyk.geekhubweatherforecast.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thegeekhub.vbilyk.geekhubweatherforecast.adapters.ForecastAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Temp;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Weather;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.DetailsFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int WEATHER_THREE_HOURS = 3;
    public static final int WEATHER_DAILY = 7;
    private ForecastAdapter adapter;
    private boolean landSW600dp;
    private Realm realm;
    public static final String APP_ID = "ad8683cdea16dddedadcdac0f4a20385";
    private String cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(MainActivity.this);

        landSW600dp = findViewById(R.id.frame_weather_details) != null;

        ListView listView = (ListView) findViewById(R.id.list_forecast);
        listView.setOnItemClickListener(this);
        adapter = new ForecastAdapter(this);
        listView.setAdapter(adapter);
        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRequest();
            }
        });
        initRequest();
    }

    private void initRequest() {
        if (isNetworkAvailable()) {
            new AsyncHttpClient().get(getUrl(true), new BaseJsonHttpResponseHandler<Response>() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
                    Log.d("onSuccess", String.valueOf(rawJsonResponse));
                    realm.beginTransaction();
                    realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).equalTo("type", WEATHER_DAILY).findAll().clear();
                    realm.copyToRealm(response.getList());
                    realm.commitTransaction();
                    new AsyncHttpClient().get(getUrl(false), new BaseJsonHttpResponseHandler<Response>() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
                            Log.d("onSuccess", String.valueOf(rawJsonResponse));
                            realm.beginTransaction();
                            realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).equalTo("type", WEATHER_THREE_HOURS).findAll().clear();
                            realm.copyToRealm(response.getList());
                            realm.commitTransaction();
                            fillList();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {

                        }

                        @Override
                        protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                            return MainActivity.this.parseResponse(rawJsonData);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {
                    Log.d("onFailure", String.valueOf(rawJsonData));
//                    initListView(realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).findAll());
                }

                @Override
                protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    return MainActivity.this.parseResponse(rawJsonData);
                }
            });
        } else {
//            initListView(realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).findAll());
        }
    }

    private void fillList() {
        RealmResults<Forecast> dailyForecast = realm.where(Forecast.class).equalTo("type", WEATHER_DAILY).equalTo("city", Integer.parseInt(cityId)).findAll();
        adapter.addAll(dailyForecast);
        adapter.notifyDataSetChanged();
    }

    private String getUrl(boolean daily) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = sharedPref.getString("city", "Kiev");
        switch (cityName) {
            default:
            case "Kiev":
                cityId = "703448";
                break;
            case "Lviv":
                cityId = "702550";
                break;
            case "Cherkasy":
                cityId = "710791";
                break;
            case "Kharkiv":
                cityId = "706483";
                break;
            case "Dnipropetrovsk":
                cityId = "709930";
                break;
            case "Chernihiv":
                cityId = "710735";
                break;
            case "Odessa":
                cityId = "698740";
                break;
            case "Kherson":
                cityId = "706448";
                break;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendQueryParameter("APPID", APP_ID)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("id", cityId);
        if (daily) builder.appendPath("daily");
        if (daily) builder.appendQueryParameter("cnt", "5");
        return builder.build().toString();
    }

    @NonNull
    private Response parseResponse(String rawJsonData) throws JSONException {
        Response response = new Response();
        Random random = new Random();
        JSONObject jsonResponse = new JSONObject(rawJsonData);
        JSONArray jsonList = jsonResponse.getJSONArray("list");
        JSONObject jsonFirstForecast = jsonList.getJSONObject(0);
        final int type = jsonFirstForecast.has("dt_txt") ? WEATHER_THREE_HOURS : WEATHER_DAILY;
        Log.d(TAG, String.format("type = %d", type));
        List<Forecast> forecasts = new ArrayList<>();
        for (int i = 0; i < jsonList.length(); i++) {
            JSONObject jsonForecast = jsonList.getJSONObject(i);
            Forecast forecast = new Forecast();

            JSONObject jsonWeather = jsonForecast.getJSONArray("weather").getJSONObject(0);
            Weather weather = new Weather();
            weather.setId(jsonWeather.optLong("id"));
            weather.setMain(jsonWeather.optString("main"));
            weather.setDescription(jsonWeather.optString("description"));
            weather.setIcon(jsonWeather.optString("icon"));
            forecast.setWeather(weather);

            forecast.setDate(new Date(jsonForecast.getLong("dt") * 1000L));

            Temp temp = new Temp();
            switch (type) {
                case WEATHER_DAILY:
                    forecast.setPressure(jsonForecast.getDouble("pressure"));
                    forecast.setHumidity(jsonForecast.getInt("humidity"));
                    forecast.setSpeed(jsonForecast.getDouble("speed"));
                    forecast.setDeg(jsonForecast.getInt("deg"));
                    forecast.setClouds(jsonForecast.getInt("clouds"));
                    forecast.setRain(jsonForecast.optDouble("rain"));

                    JSONObject jsonTemp = jsonForecast.getJSONObject("temp");
                    temp.setDay(jsonTemp.getDouble("day"));
                    temp.setMin(jsonTemp.getDouble("min"));
                    temp.setMax(jsonTemp.getDouble("max"));
                    temp.setNight(jsonTemp.getDouble("night"));
                    temp.setEve(jsonTemp.getDouble("eve"));
                    temp.setMorn(jsonTemp.getDouble("morn"));
                    break;
                case WEATHER_THREE_HOURS:
                    JSONObject jsonMain = jsonForecast.getJSONObject("main");
                    forecast.setPressure(jsonMain.getDouble("pressure"));
                    forecast.setHumidity(jsonMain.getInt("humidity"));


                    temp.setDay(jsonMain.getDouble("temp"));
                    temp.setMin(jsonMain.getDouble("temp_min"));
                    temp.setMax(jsonMain.getDouble("temp_max"));

                    JSONObject jsonWind = jsonForecast.getJSONObject("wind");
                    forecast.setSpeed(jsonWind.getDouble("speed"));
                    forecast.setDeg(jsonWind.getInt("deg"));

                    forecast.setRain(jsonForecast.getJSONObject("rain").optDouble("3h"));
                    forecast.setClouds(jsonForecast.getJSONObject("clouds").getInt("all"));
                    break;
            }
            forecast.setTemp(temp);


            int cityId = jsonResponse.getJSONObject("city").getInt("id");
            forecast.setCity(cityId);
            forecast.setId(random.nextInt(Integer.MAX_VALUE));
            forecast.setType(type);

            forecasts.add(forecast);
        }
        response.setList(forecasts);
        return response;
    }

    @Override
    protected void onDestroy() {
        if (realm != null) {
            realm.close();
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Forecast forecast = adapter.getItem(position);
        if (landSW600dp) {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
//            bundle.putParcelable("forecast", forecast);
            bundle.putInt("forecastId", forecast.getId());
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_weather_details, detailsFragment).commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("forecastId", forecast.getId());
//            intent.putExtra("forecast", forecast);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
