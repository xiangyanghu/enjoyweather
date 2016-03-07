package com.xyh.enjoyweather.util;

import android.text.TextUtils;

import com.xyh.enjoyweather.db.EnjoyWeatherDB;
import com.xyh.enjoyweather.model.City;
import com.xyh.enjoyweather.model.County;
import com.xyh.enjoyweather.model.Province;

/**
 * Created by 向阳湖 on 2016/3/7.
 */
public class Utility {
    //解析和处理服务器返回的省级数据
    public synchronized static boolean handleProvinceReponse(EnjoyWeatherDB weatherDB, String reponse) {
        if (!TextUtils.isEmpty(reponse)) {
            String[] provinces = reponse.split(",");
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
    public synchronized static boolean handleCityReponse(EnjoyWeatherDB weatherDB, String reponse, int provinceId) {
        if (!TextUtils.isEmpty(reponse)) {
            String[] cities = reponse.split(",");
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
    public synchronized static boolean handleCountyReponse(EnjoyWeatherDB weatherDB, String reponse, int cityId) {
        if (!TextUtils.isEmpty(reponse)) {
            String[] counties = reponse.split(",");
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
}
