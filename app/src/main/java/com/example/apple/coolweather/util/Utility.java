package com.example.apple.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.apple.coolweather.db.CoolWeatherDB;
import com.example.apple.coolweather.model.City;
import com.example.apple.coolweather.model.Country;
import com.example.apple.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by apple on 16/11/18.
 */
    public  class Utility{
        //解析和处理服务器返回的省级数据
        public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
            if (!TextUtils.isEmpty(response)){
                String[] allProvinces = response.split(",");
                if (allProvinces!=null&&allProvinces.length>0){
                    for (String p : allProvinces){
                        String[] array = p.split("\\|");
                        Province province = new Province();
                        province.setProvinceCode(array[0]);
                        province.setProvinceName(array[1]);
                        coolWeatherDB.saveProvince(province);

                    }
                    return true;
                }
            }
            return false;
        }

        //解析和处理服务器返回的市级数据
        public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
           if (!TextUtils.isEmpty(response)){
               String[] allCities = response.split(",");
               for (String c : allCities){
                   String[] array = c.split("\\|");
                   City city = new City();
                   city.setCityCode(array[0]);
                   city.setCityName(array[1]);
                   coolWeatherDB.saveCity(city);

               }
               return true;

           }
            return false;

        }

        //解析和处理服务器返回的县级数据
        public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
            if (!TextUtils.isEmpty(response)){
                String[] allCounties = response.split(",");
                if (allCounties!=null&&allCounties.length>0){
                    for (String c : allCounties){
                        String[] array = c.split("\\|");
                        Country country = new Country();
                        country.setCountryCode(array[0]);
                        country.setCountryName(array[1]);
                        country.setCityId(cityId);
                        coolWeatherDB.saveCountry(country);
                    }

                    return true;
                }
            }
            return false;
        }

    //解析json并存储
    public static void handleWeatherResponse(Context context, String response){
        try {
            JSONObject jsonObject = new  JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }catch (JSONException e){
            e.printStackTrace();

        }

    }

    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.commit();
    }

    }

