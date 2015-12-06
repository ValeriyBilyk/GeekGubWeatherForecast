package org.thegeekhub.vbilyk.geekhubweatherforecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.adapters.ForecastDetailsAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.PreferenceHelper;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailsFragment extends Fragment {
    private Realm realm;
    private View header;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        header = inflater.inflate(R.layout.header_details, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(PreferenceHelper.getInstance(getActivity()).getCityName());


        realm = Realm.getInstance(getActivity());
        Forecast forecast = realm.where(Forecast.class).equalTo("id", getArguments().getInt(Forecast.class.getSimpleName())).findFirst();

        ListView listView = (ListView) view.findViewById(R.id.list_forecast);
        View empty = view.findViewById(R.id.txt_empty);

        TextView txtTime = (TextView) header.findViewById(R.id.txt_time);
        TextView txtTempMax = (TextView) header.findViewById(R.id.txt_temp_max);
        TextView txtTempMin = (TextView) header.findViewById(R.id.txt_temp_min);
        TextView txtRain = (TextView) header.findViewById(R.id.txt_rain);
        TextView txtWind = (TextView) header.findViewById(R.id.txt_wind);
        TextView txtPressure = (TextView) header.findViewById(R.id.txt_pressure);
        TextView txtDescription = (TextView) header.findViewById(R.id.txt_description);

        txtTime.setText(R.string.time_now);
        txtTempMax.setText(String.format("%d°", Math.round(forecast.getTemp().getMax())));
        txtTempMin.setText(String.format("%d°", Math.round(forecast.getTemp().getMin())));
        txtRain.setText(String.valueOf(forecast.getRain()));
        txtPressure.setText(String.valueOf(Math.round(forecast.getPressure())));
        txtWind.setText(String.valueOf(Math.round(forecast.getSpeed())));
        txtDescription.setText(forecast.getWeather().getDescription());


        String iconUrl = String.format(Utils.ICON_URL, forecast.getWeather().getIcon());
        ImageView imgIcon = (ImageView) header.findViewById(R.id.img_icon);
        Picasso.with(getActivity())
                .load(iconUrl)
                .into(imgIcon);

        ForecastDetailsAdapter adapter = new ForecastDetailsAdapter(getActivity());
        listView.setAdapter(adapter);
        RealmResults<Forecast> detailsForecast = realm
                .where(Forecast.class)
                .equalTo("type", Utils.WEATHER_THREE_HOURS)
                .equalTo("city", forecast.getCity())
                .between("date", new Date(forecast.getDate().getTime() - Utils._11_HOURS), new Date(forecast.getDate().getTime() - Utils._11_HOURS + Utils.DAY))
                .findAll();
        adapter.addAll(detailsForecast);
        adapter.notifyDataSetChanged();
        if (detailsForecast.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        }
        listView.addHeaderView(header);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (realm != null) {
            realm.close();
        }
    }
}