package com.example.apple.coolweather.util;

import android.text.TextUtils;

import com.example.apple.coolweather.db.CoolWeatherDB;
import com.example.apple.coolweather.model.City;
import com.example.apple.coolweather.model.Country;
import com.example.apple.coolweather.model.Province;

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

    }

