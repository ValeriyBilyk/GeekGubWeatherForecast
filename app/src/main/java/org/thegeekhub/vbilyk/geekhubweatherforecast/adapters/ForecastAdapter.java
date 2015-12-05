package org.thegeekhub.vbilyk.geekhubweatherforecast.adapters;

import android.content.Context;
import android.text.TextUtils;
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

public class ForecastAdapter extends BaseAdapter {

    public static final String ICON_URL = "http://openweathermap.org/img/w/%s.png";
    private final Context context;
    private final LayoutInflater inflater;

    private final List<Forecast> items;

    public ForecastAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.item_forecast, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Forecast forecast = getItem(position);
        String iconUrl = String.format(ICON_URL, forecast.getWeather().getIcon());
        Picasso.with(context)
                .load(iconUrl)
                .into(holder.image);

        holder.txtTempMax.setText(String.format("%d°", Math.round(forecast.getTemp().getMax())));
        holder.txtTempMin.setText(String.format("%d°", Math.round(forecast.getTemp().getMin())));
        String date = new SimpleDateFormat("E, dd MMMM", Locale.getDefault()).format(forecast.getDate());
        holder.txtDate.setText(Character.toUpperCase(date.charAt(0)) + date.substring(1));
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView txtDate, txtTempMax, txtTempMin;

        public ViewHolder(View convertView) {
            txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            txtTempMax = (TextView) convertView.findViewById(R.id.txt_temp_max);
            txtTempMin = (TextView) convertView.findViewById(R.id.txt_temp_min);
            image = (ImageView) convertView.findViewById(R.id.img_icon);
        }
    }
}
