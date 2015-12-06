package org.thegeekhub.vbilyk.geekhubweatherforecast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.thegeekhub.vbilyk.geekhubweatherforecast.R;
import org.thegeekhub.vbilyk.geekhubweatherforecast.adapters.ForecastAdapter;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Forecast;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.Response;
import org.thegeekhub.vbilyk.geekhubweatherforecast.entities.TimeUpdated;
import org.thegeekhub.vbilyk.geekhubweatherforecast.fragments.DetailsFragment;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.PreferenceHelper;
import org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

import static org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils.WEATHER_DAILY;
import static org.thegeekhub.vbilyk.geekhubweatherforecast.utils.Utils.WEATHER_THREE_HOURS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int VIEW_LIST = 0;
    public static final int VIEW_PROGRESS = 1;
    public static final int VIEW_EMPTY = 2;

    private ForecastAdapter adapter;
    private boolean landSW600dp;
    private Realm realm;
    private int cityId;
    private BaseJsonHttpResponseHandler<Response> handler = new BaseJsonHttpResponseHandler<Response>() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Response response) {
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
                    txtUpdated.setVisibility(View.GONE);
                    fillList();
                    break;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Response errorResponse) {
            Toast.makeText(MainActivity.this, String.format("Failure, code = %d", statusCode), Toast.LENGTH_SHORT).show();
            fillList();
        }

        @Override
        protected Response parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
            return Utils.parseResponse(rawJsonData, isFailure);
        }
    };
    private ViewFlipper viewFlipper;
    private SwipeRefreshLayout refreshLayout;
    private TextView txtUpdated;
    private boolean needToClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(MainActivity.this);

        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        findViewById(R.id.btn_update_now).setOnClickListener(this);

        txtUpdated = (TextView) findViewById(R.id.txt_data_updated);

        landSW600dp = findViewById(R.id.frame_weather_details) != null;

        ListView listView = (ListView) findViewById(R.id.list_forecast);
        listView.setOnItemClickListener(this);
        adapter = new ForecastAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCity();
        needToClear = true;
        initRequest();
    }

    private void updateCity() {
        cityId = PreferenceHelper.getInstance(this).getCityId();
        setTitle(String.format("%s | %s", PreferenceHelper.getInstance(this).getCityName(), getString(R.string.app_name)));
    }

    private void initRequest() {
        if (Utils.isNetworkAvailable(this)) {
            if (adapter.isEmpty()) viewFlipper.setDisplayedChild(VIEW_PROGRESS);
            refreshLayout.setRefreshing(true);
            new AsyncHttpClient().get(Utils.getWeatherUrl(cityId, true), handler);
        } else {
            Utils.noInternetToast();
            TimeUpdated timeUpdated = realm.where(TimeUpdated.class).equalTo("city", cityId).findFirst();
            if (timeUpdated != null) {
                String date = new SimpleDateFormat("yyyy.MM.dd, HH:mm", Locale.getDefault()).format(timeUpdated.getUpdatedAt());
                txtUpdated.setText(String.format(getString(R.string.text_data_updated), date));
                txtUpdated.setVisibility(View.VISIBLE);
            }
            fillList();
        }
    }

    private void fillList() {
        RealmResults<Forecast> dailyForecast = realm.where(Forecast.class).equalTo("type", WEATHER_DAILY).equalTo("city", cityId).findAll();
        if (needToClear) {
            adapter.clear();
            needToClear = false;
        }
        adapter.addAll(dailyForecast);
        adapter.notifyDataSetChanged();
        if (dailyForecast.isEmpty()) {
            viewFlipper.setDisplayedChild(VIEW_EMPTY);
        } else {
            viewFlipper.setDisplayedChild(VIEW_LIST);
        }
        refreshLayout.setRefreshing(false);
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
            bundle.putInt(Forecast.class.getSimpleName(), forecast.getId());
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_weather_details, detailsFragment).commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Forecast.class.getSimpleName(), forecast.getId());
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

    @Override
    public void onRefresh() {
        needToClear = true;
        initRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_now:
                onRefresh();
                break;
        }
    }
}
