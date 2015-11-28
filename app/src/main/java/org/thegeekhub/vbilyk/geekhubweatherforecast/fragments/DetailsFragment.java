package org.thegeekhub.vbilyk.geekhubweatherforecast.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class DetailsFragment extends Fragment {

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int forecastId = getArguments().getInt("forecastId");

        Forecast forecast = Realm.getInstance(getActivity()).where(Forecast.class).equalTo("id", forecastId).findFirst();

        TextView detailsTime = (TextView) view.findViewById(R.id.details_time);
        TextView detailsDate = (TextView) view.findViewById(R.id.details_date);
        TextView detailsTemperatureValue = (TextView) view.findViewById(R.id.details_temperature_value);
        TextView detailsDescriptionValue = (TextView) view.findViewById(R.id.details_description_value);
        TextView detailsWindSpeedValue = (TextView) view.findViewById(R.id.details_wind_speed_value);
        TextView detailsHumidityValue = (TextView) view.findViewById(R.id.details_humidity_value);
        TextView detailsPressureValue = (TextView) view.findViewById(R.id.details_pressure_value);
        ImageView image = (ImageView) view.findViewById(R.id.details_icon);

        DateFormat formatTime = new SimpleDateFormat("HH:mm");
        DateFormat formatDate = new SimpleDateFormat("dd/MM/EEEEE");
        Date date = forecast.getDate();

        String time = formatTime.format(date);
        String dateWeek = formatDate.format(date);
        String temperature = String.format("%.2f° C", forecast.getMain().getTemp());
        String description = forecast.getWeather().get(0).getDescription();
        String windSpeed = String.valueOf(forecast.getWind().getSpeed() + "m/s");
        String humidity = String.valueOf(forecast.getMain().getHumidity() + "%");
        String pressure = String.valueOf(forecast.getMain().getPressure());

        detailsTime.setText(time);
        detailsDate.setText(dateWeek);
        detailsTemperatureValue.setText(temperature);
        detailsDescriptionValue.setText(description);
        detailsWindSpeedValue.setText(windSpeed);
        detailsHumidityValue.setText(humidity);
        detailsPressureValue.setText(pressure);
        String iconId = forecast.getWeather().get(0).getIcon();
        String iconUrl = String.format("http://openweathermap.org/img/w/%s.png", iconId);
        Picasso.with(context)
                .load(iconUrl)
                .into(image);

    }

}