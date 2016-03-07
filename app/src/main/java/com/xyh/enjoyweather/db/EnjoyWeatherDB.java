package com.xyh.enjoyweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xyh.enjoyweather.model.City;
import com.xyh.enjoyweather.model.County;
import com.xyh.enjoyweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 向阳湖 on 2016/3/7.
 */
public class EnjoyWeatherDB {
    //数据库名字
    public static final String DB_NAME = "enjoy_weather";

    //私有静态类,不能加final.只有成员才能会用final修饰
    private static EnjoyWeatherDB enjoyWeatherDB;

    //数据库
    private SQLiteDatabase db;

    //数据库版本
    private static final int VERSION = 1;

    //私有构造方法
    private EnjoyWeatherDB(Context context) {
        EnjoyWeatherOpenHelper dbOpenHelper = new EnjoyWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbOpenHelper.getWritableDatabase();
    }

    //获取EnjoyWeather的实例
    public synchronized static EnjoyWeatherDB getInstance(Context context) {
        if (enjoyWeatherDB == null) {
            enjoyWeatherDB = new EnjoyWeatherDB(context);
        }
        return enjoyWeatherDB;
    }

    //将Province实例存储到数据库中
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    //从数据库中读取数据
    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            }while(cursor.moveToNext());
        }
        return provinceList;
    }

    //将City实例存储到数据库中
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    //从数据库中读取数据
    public List<City> loadCitys(int provinceId) {
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            }while(cursor.moveToNext());
        }
        return cityList;
    }

    //将County实例存储到数据库中
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County", null, values);
        }
    }

    //从数据库中读取数据
    public List<County> loadCountys(int cityId) {
        List<County> countyList = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            }while(cursor.moveToNext());
        }
        return countyList;
    }
}
