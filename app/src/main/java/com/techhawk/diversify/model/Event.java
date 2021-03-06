package com.techhawk.diversify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Yidi Wu on 15/4/18.
 */

@IgnoreExtraProperties
public class Event implements Parcelable {

    // Instance variable
    private String name;
    private String comments;
    private String celebration;
    private String imgUrl;
    private String month;
    private String date;
    private String location;

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCelebration() {
        return celebration;
    }

    public void setCelebration(String celebration) {
        this.celebration = celebration;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.comments);
        dest.writeString(this.celebration);
        dest.writeString(this.imgUrl);
        dest.writeString(this.month);
        dest.writeString(this.date);
        dest.writeString(this.location);
    }

    protected Event(Parcel in) {
        this.name = in.readString();
        this.comments = in.readString();
        this.celebration = in.readString();
        this.imgUrl = in.readString();
        this.month = in.readString();
        this.date = in.readString();
        this.location = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
