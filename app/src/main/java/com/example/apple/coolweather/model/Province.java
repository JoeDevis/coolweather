package com.example.apple.coolweather.model;

/**
 * Created by apple on 16/11/18.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public int getId() {

        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
