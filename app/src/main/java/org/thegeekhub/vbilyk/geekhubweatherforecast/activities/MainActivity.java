package org.thegeekhub.vbilyk.geekhubweatherforecast.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.thegeekhub.vbilyk.geekhubweatherforecast.ForecastAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Main;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Weather;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Wind;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.DetailsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ForecastAdapter adapter;
    private boolean landSW600dp;
    private Realm realm;
    private ListView listView;
    private TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(MainActivity.this);

        landSW600dp = findViewById(R.id.frame_weather_details) != null;

        findViewById(R.id.text_time_of_weather);

//        Forecast parcelableExtra = getIntent().getParcelableExtra(Forecast.class.getSimpleName());

        listView = (ListView) findViewById(R.id.listForecast);
        head = (TextView) findViewById(R.id.head);

//        String url = "http://api.openweathermap.org/data/2.5/forecast?APPID=ad8683cdea16dddedadcdac0f4a20385&id=703448&units=metric";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String cityId;
        String cityName = sharedPref.getString("city", "Kiev");
        head.setText(cityName);
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
        String appId = "ad8683cdea16dddedadcdac0f4a20385";
        String forecast = "forecast";
        String url = new Uri.Builder()
                .scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath(forecast)
                .appendQueryParameter("APPID", appId)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("id", cityId)
                .build().toString();

        if (isNetworkAvailable()) {
            new AsyncHttpClient().get(url, new BaseJsonHttpResponseHandler<Response>() {

//            private Gson gson;

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
                    Log.d("onSuccess", String.valueOf(rawJsonResponse));
                    List<Forecast> forecastList = response.getList();
                    initListView(forecastList);
                    realm.beginTransaction();
                    realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).findAll().clear();
//                    for (Forecast cForecast : cityList) {
//                        cForecast.removeFromRealm();
//                    }
                    realm.copyToRealm(forecastList);
                    realm.commitTransaction();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {
                    Log.d("onFailure", String.valueOf(rawJsonData));
                    initListView(realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).findAll());
                }

                @Override
                protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
//                if (gson == null) {
//                    gson = new GsonBuilder()
//                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                            .create();
//                }
//                return gson.fromJson(rawJsonData, Response.class);
                    Random random = new Random();
                    JSONObject jsonResponse = new JSONObject(rawJsonData);
                    Response response = new Response();
                    JSONArray jsonList = jsonResponse.getJSONArray("list");
                    List<Forecast> forecasts = new ArrayList<>();
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonForecast = jsonList.getJSONObject(i);
                        Forecast forecast = new Forecast();
                        forecast.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(jsonForecast.optString("dt_txt")));

                        JSONObject jsonWind = jsonForecast.getJSONObject("wind");
                        Wind wind = new Wind();
                        wind.setDeg(jsonWind.getDouble("deg"));
                        wind.setSpeed(jsonWind.getDouble("speed"));
                        forecast.setWind(wind);

                        JSONObject jsonWeather = jsonForecast.getJSONArray("weather").getJSONObject(0);
                        RealmList<Weather> weathers = new RealmList<>();
                        Weather weather = new Weather();
                        weather.setId(jsonWeather.optLong("id"));
                        weather.setMain(jsonWeather.optString("main"));
                        weather.setDescription(jsonWeather.optString("description"));
                        weather.setIcon(jsonWeather.optString("icon"));
                        weathers.add(weather);
                        forecast.setWeather(weathers);

                        JSONObject jsonMain = jsonForecast.optJSONObject("main");
                        Main main = new Main();
                        if (jsonMain != null) {
                            main.setTemp(jsonMain.getDouble("temp"));
                            main.setTempMin(jsonMain.getDouble("temp_min"));
                            main.setTempMax(jsonMain.getDouble("temp_max"));
                            main.setPressure(jsonMain.getDouble("pressure"));
                            main.setSeaLevel(jsonMain.getDouble("sea_level"));
                            main.setGrndLevel(jsonMain.getDouble("grnd_level"));
                            main.setHumidity(jsonMain.getInt("humidity"));
                            main.setTempKf(jsonMain.getDouble("temp_kf"));
                            forecast.setMain(main);
                        }


                        int cityId = jsonResponse.getJSONObject("city").getInt("id");
                        forecast.setCity(cityId);

                        forecast.setId(random.nextInt(Integer.MAX_VALUE));

                        forecasts.add(forecast);
                    }
                    response.setList(forecasts);
                    return response;
                }
            });
        } else {
            initListView(realm.where(Forecast.class).equalTo("city", Integer.parseInt(cityId)).findAll());
        }
        listView.setOnItemClickListener(this);
    }

    private void initListView(List<Forecast> forecastList) {
        adapter = new ForecastAdapter(MainActivity.this, forecastList);
        listView.setAdapter(adapter);
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
