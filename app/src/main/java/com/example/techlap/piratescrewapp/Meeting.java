package com.example.techlap.piratescrewapp;

/**
 * Created by tech lap on 20/11/2016.
 */
public class Meeting {
    String date,dsecPlace;
    double lat,lang;

    public String getDsecPlace() {
        return dsecPlace;
    }

    public void setDsecPlace(String dsecPlace) {
        this.dsecPlace = dsecPlace;
    }

    public Meeting() {
    }

    public Meeting(String date, String dsecPlace, double lat, double lang) {
        this.date = date;
        this.dsecPlace = dsecPlace;
        this.lat = lat;
        this.lang = lang;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }
}
