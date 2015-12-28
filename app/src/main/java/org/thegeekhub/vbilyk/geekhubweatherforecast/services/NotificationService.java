package org.thegeekhub.vbilyk.geekhubweatherforecast.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.activities.MainActivity;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.TimeUpdated;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.PreferenceHelper;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

import static org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils.WEATHER_DAILY;
import static org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils.WEATHER_THREE_HOURS;


public class NotificationService extends Service {
    public static final int WEATHER_TIME_UPDATE_HOURS = 3600;

    private Context mContext;
    private Realm realm;
    private int cityId;
    private BaseJsonHttpResponseHandler<Response> handler = new BaseJsonHttpResponseHandler<Response>() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
            if (realm == null || realm.isClosed()) return;
            switch (response.getList().get(0).getType()) {
                case WEATHER_DAILY:
                    realm.beginTransaction();
                    realm.where(Forecast.class).equalTo("city", cityId).equalTo("type", WEATHER_DAILY).findAll().clear();
                    realm.copyToRealm(response.getList());
                    realm.commitTransaction();
                    new AsyncHttpClient().get(Utils.getWeatherUrl(cityId, false), handler);
                    break;
                case WEATHER_THREE_HOURS:
                    realm.beginTransaction();
                    realm.where(Forecast.class).equalTo("city", cityId).equalTo("type", WEATHER_THREE_HOURS).findAll().clear();
                    realm.copyToRealm(response.getList());
                    realm.copyToRealmOrUpdate(new TimeUpdated(cityId));
                    realm.commitTransaction();
                    break;
            }
            showNotification();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {
            Toast.makeText(mContext, String.format("Failure, code = %d", statusCode), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
            return Utils.parseResponse(rawJsonData, isFailure);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        cityId = PreferenceHelper.getInstance(this).getCityId();
        mContext = getApplicationContext();
        realm = Realm.getInstance(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sendRequest();
                        TimeUnit.SECONDS.sleep(WEATHER_TIME_UPDATE_HOURS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendRequest() {
        if (Utils.isNetworkAvailable(this)) {
            new AsyncHttpClient().get(Utils.getWeatherUrl(cityId, true), handler);
        }
    }

    private void showNotification() {
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_refresh)
                        // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_cloud_queue))
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Данные были обновлены")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Погода")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Обновление погоды"); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (Utils.isNetworkAvailable(mContext)) {
//            NotificationUtil.callUpdateNotification(mContext);
//        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
