package com.example.apple.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apple.coolweather.model.City;
import com.example.apple.coolweather.model.Country;
import com.example.apple.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/11/18.
 */
public class CoolWeatherDB {
    /*
        数据库名
     */
    public static final String DB_NAME = "cool_weather";

    /*
        数据库版本
     */
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    //将构造方法私有化
    private  CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();

    }

    //获取CoolWeatherDB的实例
    public synchronized static CoolWeatherDB getCoolWeatherDB(Context context){

        if (coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return  coolWeatherDB;
    }

    //将Province实例存储到数据库
    public void  saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);

        }

    }

    //从数据库读取全国所有的省份信息

    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());

        }
        if (cursor!=null) cursor.close();
        return list;

    }

    //将City实例存储到数据库
    public void saveCity(City city){
        if (city!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);

        }
    }

    //从数据库读取某省下所有的城市信息。
    public List<City> loadCity(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"privince_id?",new String[]{String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()){

            do {

                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());

        }
        if (cursor!=null) cursor.close();

        return list;

    }

    //将Country实例存储到数据库
    public void saveCountry(Country country){
        if (country!=null){
            ContentValues values = new ContentValues();
            values.put("country_name",country.getCountryName());
            values.put("country_code",country.getCountryCode());
            values.put("city_id",country.getCityId());
            db.insert("Country",null,values);


        }

    }

    //从数据库中读取所有县的信息
    public  List<Country> loadCounties(int cityId){
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = db.query("Country",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null,null);

        if (cursor.moveToFirst()){
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCityId(cityId);
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("cuuntry_code")));
                list.add(country);
            }   while (cursor.moveToNext());

        }

        return list;
    }
}
