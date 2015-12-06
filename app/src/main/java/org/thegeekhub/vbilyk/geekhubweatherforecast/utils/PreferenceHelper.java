package org.thegeekhub.vbilyk.geekhubweatherforecast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.applications.App;

public class PreferenceHelper {

    private static PreferenceHelper helper;
    private final SharedPreferences sharedPreferences;

    private PreferenceHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceHelper getInstance(Context context) {
        if (helper == null) {
            helper = new PreferenceHelper(context);
        }
        return helper;
    }

    public int getCityPosition() {
        return Integer.parseInt(sharedPreferences.getString(App.getAppContext().getString(R.string.city_key), String.valueOf(App.getAppContext().getString(R.string.city_default))));
    }

    public String getCityName() {
        return App.getAppContext().getResources().getStringArray(R.array.cities)[getCityPosition()];
    }

    public int getCityId() {
        return Utils.getCityIdByPosition(getCityPosition());
    }
}
