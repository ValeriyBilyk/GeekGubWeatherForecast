package org.thegeekhub.vbilyk.geekhubweatherforecast.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForecastDetailsAdapter extends BaseAdapter {

    public static final String ICON_URL = "http://openweathermap.org/img/w/%s.png";
    private final Context context;
    private final LayoutInflater inflater;

    private final List<Forecast> items;

    public ForecastDetailsAdapter(Context context) {
        this.items = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addAll(List<Forecast> data) {
        items.addAll(data);
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Forecast getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_forecast_details, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Forecast forecast = getItem(position);

        holder.txtTime.setText(new SimpleDateFormat("HH:00", Locale.getDefault()).format(forecast.getDate()));
        holder.txtTempMax.setText(String.format("%d°", Math.round(forecast.getTemp().getMax())));
        holder.txtTempMin.setText(String.format("%d°", Math.round(forecast.getTemp().getMin())));
        holder.txtRain.setText(String.valueOf(forecast.getRain()));
        holder.txtPressure.setText(String.valueOf(Math.round(forecast.getPressure())));
        holder.txtWind.setText(String.valueOf(Math.round(forecast.getSpeed())));
        return convertView;
    }

    static class ViewHolder {
//        ImageView image;
        TextView txtTime, txtTempMax, txtTempMin, txtRain, txtPressure, txtWind;

        public ViewHolder(View convertView) {
            txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            txtTempMax = (TextView) convertView.findViewById(R.id.txt_temp_max);
            txtTempMin = (TextView) convertView.findViewById(R.id.txt_temp_min);
            txtRain = (TextView) convertView.findViewById(R.id.txt_rain);
            txtPressure = (TextView) convertView.findViewById(R.id.txt_pressure);
            txtWind = (TextView) convertView.findViewById(R.id.txt_wind);
//            image = (ImageView) convertView.findViewById(R.id.img_icon);
        }
    }
}
