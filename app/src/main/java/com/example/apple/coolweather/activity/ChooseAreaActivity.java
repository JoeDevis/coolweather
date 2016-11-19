package com.example.apple.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.coolweather.R;
import com.example.apple.coolweather.db.CoolWeatherDB;
import com.example.apple.coolweather.model.City;
import com.example.apple.coolweather.model.Country;
import com.example.apple.coolweather.model.Province;
import com.example.apple.coolweather.util.HttpUtil;
import com.example.apple.coolweather.util.Utility;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/11/18.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province>provinceList;
    private List<City>cityList;
    private List<Country>countryList;

    private Province selectProvience;
    private City selectedCity;
    private int currentLevel;

    //是否从WeatherAcivity中跳转过来
    private boolean isFromWeatherActivity;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到weatherActivity
        if (sharedPreferences.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        listView = (ListView)findViewById(R.id.list_view);
        titleView = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getCoolWeatherDB(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectProvience = provinceList.get(position);
                    queryCitys();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCountries();
                }else if (currentLevel == LEVEL_COUNTY){
                    if (isFromWeatherActivity){
                        String countyCode = countryList.get(position).getCountryCode();
                        Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        });
        queryProviences();
    }
    public  void queryProviences(){
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }

    }
    public void queryCitys(){
        cityList = coolWeatherDB.loadCity(selectProvience.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectProvience.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectProvience.getProvinceCode(),"city");
        }

    }

    public void queryCountries(){
        countryList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countryList.size()>0){
            dataList.clear();
            for (Country country : countryList){
                dataList.add(country.getCountryName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;

        }else {
            queryFromServer(selectedCity.getCityCode(),"country");
        }
    }

    private void queryFromServer(final String code, final  String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,response,selectProvience.getId());
                }else if ("country".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if (result){
                    //回到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProviences();
                            }else if ("city".equals(type)){
                                queryCitys();
                            }else if ("country".equals(type)){
                                queryCountries();
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
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private  void  closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel==LEVEL_COUNTY){
            queryCitys();
        }else if (currentLevel==LEVEL_CITY){
            queryProviences();
        }else finish();
    }
}
