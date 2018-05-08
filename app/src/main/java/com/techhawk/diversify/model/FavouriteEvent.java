package com.techhawk.diversify.model;

/**
 * Created by Yidi Wu on 6/5/18.
 */

public class FavouriteEvent {

    private String name;
    private String desc;
    private String date;
    private String location;

    public FavouriteEvent() {
    }

    public FavouriteEvent(String name, String desc, String date, String location) {
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
