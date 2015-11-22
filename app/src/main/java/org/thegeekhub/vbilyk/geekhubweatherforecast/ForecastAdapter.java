package org.thegeekhub.vbilyk.geekhubweatherforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ForecastAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    private final List<Forecast> forecastList;

    public ForecastAdapter(Context context, List<Forecast> forecastList) {
        this.forecastList = forecastList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return forecastList.size();
    }

    @Override
    public Forecast getItem(int position) {
        return forecastList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_forecast, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Forecast item = getItem(position);

        DateFormat formatTime = new SimpleDateFormat("HH:mm");
        DateFormat formatDate = new SimpleDateFormat("dd/MM/EEEEE");
        Date date = item.getDate();

        String time = formatTime.format(date);
        String dateWeek = formatDate.format(date);
        String condition = item.getWeather().get(0).getMain();
        String temperature = String.format("%.2f° C", item.getMain().getTemp());
        String iconId = item.getWeather().get(0).getIcon();
        String iconUrl = String.format("http://openweathermap.org/img/w/%s.png", iconId);
        Picasso.with(context)
                .load(iconUrl)
                .into(viewHolder.image);

        viewHolder.textTimeOfWeather.setText(time);
        viewHolder.textDateOfWeather.setText(dateWeek);
        viewHolder.textCondition.setText(condition);
        viewHolder.textTemperature.setText(temperature);

        return convertView;
    }

    static class ViewHolder {
        TextView textTimeOfWeather;
        TextView textDateOfWeather;
        TextView textCondition;
        TextView textTemperature;
        ImageView image;

        public ViewHolder(View convertView) {
            textTimeOfWeather = (TextView) convertView.findViewById(R.id.text_time_of_weather);
            textDateOfWeather = (TextView) convertView.findViewById(R.id.date_of_weather);
            textCondition = (TextView) convertView.findViewById(R.id.condition);
            textTemperature = (TextView) convertView.findViewById(R.id.temperature);
            image = (ImageView) convertView.findViewById(R.id.icon);
        }
    }
}
