package com.example.apple.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 16/11/18.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /*
     * Province 表建语句
     */
    public static final String CREATE_PROVINCE = "creat table Province("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";

    /*
        City
     */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key antoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";

    /*
        Country
     */
    public static final String CREATE_COUNTRY = "create table Country ("
            + "id integer primary key autoincrement,"
            + "country_name text,"
            + "country_code text,"
            + "city_id integer)";

   public CoolWeatherOpenHelper(Context context, String name , SQLiteDatabase.CursorFactory cursorFactory,int version){

        super(context,name,cursorFactory,version);
   }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
