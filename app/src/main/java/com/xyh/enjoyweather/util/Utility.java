package com.xyh.enjoyweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.xyh.enjoyweather.db.EnjoyWeatherDB;
import com.xyh.enjoyweather.model.City;
import com.xyh.enjoyweather.model.County;
import com.xyh.enjoyweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 向阳湖 on 2016/3/7.
 */
public class Utility {
    //解析和处理服务器返回的省级数据
    public synchronized static boolean handleProvinceResponse(EnjoyWeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] provinces = response.split(",");
            if (provinces != null && provinces.length > 0) {
                //将provinces数组的基本类型变成String类型,以便分割
                for (String p : provinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据保存到Province
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public synchronized static boolean handleCityResponse(EnjoyWeatherDB weatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if (cities != null && cities.length > 0) {
                //将cities数组的基本类型变成String类型,以便分割
                for (String c : cities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据保存到City
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public synchronized static boolean handleCountyResponse(EnjoyWeatherDB weatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if (counties != null && counties.length > 0) {
                //将counties数组的基本类型变成String类型,以便分割
                for (String c : counties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据保存到County
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    //解析服务器返回的JSON数据,并将解析出的数据存储到本地
    public static void handleWeatherResponse(Context context, String response) {
        try {
            Log.d("utility", "----------handleWeatherResponse()执行");
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            //getString("key")这些key从哪里来的,是规定的!!!
            String cityName =  weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String time = weatherInfo.getString("ptime");
            String weather = weatherInfo.getString("weather");
            String maxTemp = weatherInfo.getString("temp1");
            String minTemp = weatherInfo.getString("temp2");
            saveWeatherInfo(context, cityName, weatherCode, time, weather, minTemp, maxTemp);
            Log.d("utility", "----------调用了saveWeatherInfo()方法");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //将服务器返回的所有天气信息存储到SharedPreferences文件中
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String time, String weather, String minTemp, String maxTemp) {
        Log.d("Utility","------saveWeatherInfo()执行");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        //获得SharePreferences文件
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("time", time);
        editor.putString("weather", weather);
        editor.putString("min_temp", minTemp);
        editor.putString("max_temp", maxTemp);
        editor.putString("date", sdf.format(new Date()));
        Boolean isSave = editor.commit();
        Log.d("Utility", "----------" + isSave);
    }
}
