package com.xyh.enjoyweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.enjoyweather.R;
import com.xyh.enjoyweather.db.EnjoyWeatherDB;
import com.xyh.enjoyweather.model.City;
import com.xyh.enjoyweather.model.County;
import com.xyh.enjoyweather.model.Province;
import com.xyh.enjoyweather.util.HttpCallbackListener;
import com.xyh.enjoyweather.util.HttpUtil;
import com.xyh.enjoyweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 向阳湖 on 2016/3/7.
 */
public class ChooseAreaActivity extends Activity {
    //设置地区等级
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    //定义一些控件,类
    private TextView tvTitle;
    private ListView lvArea;
    private ProgressDialog dialog;
    private EnjoyWeatherDB weatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        initView();
        dataList = new ArrayList<String>();
        weatherDB = EnjoyWeatherDB.getInstance(this);
        //初始化适配器
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        lvArea.setAdapter(adapter);
        lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        //加载省级数据
        queryProvinces();
    }

    //加载省级数据,先从数据库查找,若是没有再联网查询
    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //刷新数据
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            tvTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null,"province");
        }
    }

    //加载市级数据
    private void queryCities() {
        cityList = weatherDB.loadCitys(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            tvTitle.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    ////加载县级数据
    private void queryCounties() {
        countyList = weatherDB.loadCountys(selectedCity.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            tvTitle.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }

    //根据传入的代号和类型从服务器上查询省市县数据
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String reponse) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceReponse(weatherDB, reponse);
                }else if ("city".equals(type)) {
                    result = Utility.handleCityReponse(weatherDB, reponse,selectedProvince.getId());
                }else if ("county".equals(type)) {
                    result = Utility.handleCountyReponse(weatherDB, reponse, selectedCity.getId());
                }

                if (result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if ("province".equals(type)) {
                                queryProvinces();
                            }else if ("city".equals(type)) {
                                queryCities();
                            }else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示进度对话框
    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    //关闭进度对话框
    private void closeProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //捕获back按钮,根据当前等级判断,此时应该返回到省,市列表还是直接退出
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        }else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        lvArea = (ListView) findViewById(R.id.lv_area);
    }
}
