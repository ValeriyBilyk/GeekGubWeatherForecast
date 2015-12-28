package org.thegeekhub.vbilyk.geekhubweatherforecast.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.applications.App;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Temp;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Weather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {
    public static final int WEATHER_THREE_HOURS = 3;
    public static final int WEATHER_DAILY = 7;

    public static final long DAY = 86400000L;
    public static final long _11_HOURS = 39600000L;

    public static final String APP_ID = "ad8683cdea16dddedadcdac0f4a20385";
    public static final String ICON_URL = "http://openweathermap.org/img/w/%s.png";
    private static Toast toast;


    public static int getCityIdByPosition(int position) {
        switch (position) {
            default:
            case 0:
                return 703448;
            case 1:
                return 702550;
            case 2:
                return 710791;
            case 3:
                return 706483;
            case 4:
                return 709930;
            case 5:
                return 710735;
            case 6:
                return 698740;
            case 7:
                return 706448;
        }
    }

    public static String getWeatherUrl(int cityId, boolean daily) {
        Uri.Builder builder = new Uri.Builder();
        String currentLang = App.getAppContext().getString(R.string.lang);
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendQueryParameter("APPID", Utils.APP_ID)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("lang", currentLang)
                .appendQueryParameter("id", String.valueOf(cityId));
        if (daily) builder.appendPath("daily");
        if (daily) builder.appendQueryParameter("cnt", "14");
        return builder.build().toString();
    }

    public static Response parseResponse(String rawJsonData, boolean isFailure) throws JSONException {
        if (isFailure) return null; // TODO: 06.12.15
        Response response = new Response();
        Random random = new Random(); 
        JSONObject jsonResponse = new JSONObject(rawJsonData);
        JSONArray jsonList = jsonResponse.getJSONArray("list");
        JSONObject jsonFirstForecast = jsonList.getJSONObject(0);
        final int type = jsonFirstForecast.has("dt_txt") ? WEATHER_THREE_HOURS : WEATHER_DAILY;
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

                    try {
                        forecast.setRain(jsonForecast.getDouble("rain"));
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }

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

                    try {
                        forecast.setRain(jsonForecast.getJSONObject("rain").getDouble("3h"));
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void noInternetToast() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(App.getAppContext(), R.string.text_no_internet, Toast.LENGTH_SHORT);
        toast.show();
    }
}
