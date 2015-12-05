package org.thegeekhub.vbilyk.geekhubweatherforecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thegeekhub.vbilyk.geekhubweatherforecast.activities.MainActivity;
import org.thegeekhub.vbilyk.geekhubweatherforecast.adapters.ForecastAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.adapters.ForecastDetailsAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailsFragment extends Fragment {

    public static final long DAY = 86400000L;
    public static final long _11_HOURS = 39600000L;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Details");

        int forecastId = getArguments().getInt("forecastId");

        realm = Realm.getInstance(getActivity());
        Forecast forecast = realm.where(Forecast.class).equalTo("id", forecastId).findFirst();

        TextView txtTime = (TextView) view.findViewById(R.id.txt_time);
        TextView txtTempMax = (TextView) view.findViewById(R.id.txt_temp_max);
        TextView txtTempMin = (TextView) view.findViewById(R.id.txt_temp_min);
        TextView txtRain = (TextView) view.findViewById(R.id.txt_rain);
        TextView txtWind = (TextView) view.findViewById(R.id.txt_wind);
        TextView txtPressure = (TextView) view.findViewById(R.id.txt_pressure);

        txtTime.setText("now");
        txtTempMax.setText(String.format("%d°", Math.round(forecast.getTemp().getMax())));
        txtTempMin.setText(String.format("%d°", Math.round(forecast.getTemp().getMin())));
        txtRain.setText(String.valueOf(forecast.getRain()));
        txtPressure.setText(String.valueOf(Math.round(forecast.getPressure())));
        txtWind.setText(String.valueOf(Math.round(forecast.getSpeed())));


        String iconUrl = String.format(ForecastAdapter.ICON_URL, forecast.getWeather().getIcon());
        ImageView imgIcon = (ImageView) view.findViewById(R.id.img_icon);
        Picasso.with(getActivity())
                .load(iconUrl)
                .into(imgIcon);

        ListView listView = (ListView) view.findViewById(R.id.list_forecast);
        ForecastDetailsAdapter adapter = new ForecastDetailsAdapter(getActivity());
        listView.setAdapter(adapter);
        RealmResults<Forecast> detailsForecast = realm
                .where(Forecast.class)
                .equalTo("type", MainActivity.WEATHER_THREE_HOURS)
                .equalTo("city", 703448)
                .between("date", new Date(forecast.getDate().getTime() - _11_HOURS), new Date(forecast.getDate().getTime() - _11_HOURS + DAY))
                .findAll();
        adapter.addAll(detailsForecast);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (realm != null) {
            realm.close();
        }
    }
}