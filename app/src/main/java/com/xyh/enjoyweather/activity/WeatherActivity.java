package com.xyh.enjoyweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyh.enjoyweather.R;
import com.xyh.enjoyweather.util.HttpCallbackListener;
import com.xyh.enjoyweather.util.HttpUtil;
import com.xyh.enjoyweather.util.Utility;

/**
 * Created by 向阳湖 on 2016/3/9.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout weatherLayout;
    private TextView tvCityName, tvTime, tvDate, tvWeather, tvMaxTemp, tvMinTemp;
    private Button btnSwitchCity, btnRefreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        initView();
        String countyCode = getIntent().getStringExtra("county_code");
        //有县级代号就去查询
        if (!TextUtils.isEmpty(countyCode)) {
            tvTime.setText("同步中...");
            //设置天气信息不可见
            weatherLayout.setVisibility(View.INVISIBLE);
            tvWeather.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级代码就直接显示本地天气
            showWeather();
        }

        //切换城市和更新天气
        btnSwitchCity.setOnClickListener(this);
        btnRefreshWeather.setOnClickListener(this);
    }

    private void initView() {
        weatherLayout = (LinearLayout) findViewById(R.id.weatherInfo_layout);
        tvCityName = (TextView) findViewById(R.id.tv_cityName);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvMinTemp = (TextView) findViewById(R.id.tv_minTemp);
        tvMaxTemp = (TextView) findViewById(R.id.tv_maxTemp);
        btnSwitchCity = (Button) findViewById(R.id.btn_switchCity);
        btnRefreshWeather = (Button) findViewById(R.id.btn_refreshWeather);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switchCity:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refreshWeather:
                tvTime.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                //获得天气代号,若是没有则weatherCode的值为空
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:break;
        }
    }

    //查询天气代号
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    //查询天气信息
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(final String address, final String codeType) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(codeType)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(codeType)) {
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText("同步失败!");
                    }
                });
            }
        });
    }

//    editor.putBoolean("city_selecteed", true);
//    editor.putString("city_name", cityName);
//    editor.putString("weather_code", weatherCode);
//    editor.putString("time", time);
//    editor.putString("weather", weather);
//    editor.putString("min_temp", minTemp);
//    editor.putString("max_temp", maxTemp);
//    editor.putString("date", sdf.format(new Date()));


    //显示天气
    //从SharedPreferences文件中读取天气信息,并显示到界面上
    private void showWeather() {
        //这个方法不执行
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tvCityName.setText(prefs.getString("city_name","error"));
        tvTime.setText(prefs.getString("time", "error"));
        System.out.println("22-----showWeather()执行");
        tvDate.setText(prefs.getString("date", "error"));
        tvWeather.setText(prefs.getString("weather", "error"));
        tvMinTemp.setText(prefs.getString("min_temp", "error"));
        tvMaxTemp.setText(prefs.getString("max_temp","error"));
        tvWeather.setVisibility(View.VISIBLE);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
